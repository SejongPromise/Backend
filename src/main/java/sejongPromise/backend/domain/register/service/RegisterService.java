package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.register.repository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.register.model.dto.response.ResponseMyRegisterDto;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.dto.StudentBookInfo;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongBookService;
import sejongPromise.backend.infra.sejong.service.classic.SejongRegisterService;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final SejongRegisterService sejongRegisterService;
    private final SejongBookService sejongBookService;

    /**
     * 시험 신청 서비스
     * @param studentId
     * @param dto
     */
    public void applyTest(Long studentId, RequestCreateRegisterDto dto){
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저가 존재하지 않습니다."));

        applyExceptionHandling(dto.getDate(), student);
        //todo : date, time 에 맞는 shInfo 값인지 확인 필요

        Book book = bookRepository.findByTitle(dto.getBookTitle()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서를 찾을 수 없습니다."));
        // 신청 DTO 생성.
        RequestTestApplyDto requestTestApplyDto = RequestTestApplyDto.builder()
                .bkAreaCode(book.getField().getCode().toString())
                .bkCode(book.getCode().toString())
                .shInfoId(dto.getShInfoId())
                .build();

        sejongRegisterService.applyRegister(student.getSessionToken(), requestTestApplyDto);
        log.info("시험 예약 완료");
        // todo : 시험 신청을 하면 OPAP 값을 던져주어야 한다.
        List<MyRegisterInfo> myRegisterInfos = sejongRegisterService.crawlRegisterInfo(student.getSessionToken());
        // 시험 신청은 1일 1회이므로 date 로 구별한다.
        for (MyRegisterInfo data : myRegisterInfos) {
            if (data.getDate().equals(dto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !data.getCancelOPAP().isBlank()) {
                //register 생성
                Register register = Register.builder()
                        .bookTitle(dto.getBookTitle())
                        .date(dto.getDate())
                        .startTime(dto.getTime())
                        .endTime(dto.getTime().plusMinutes(30L))
                        .year(dto.getYear())
                        .student(student)
                        .semester(Semester.of(dto.getSemester().replace(" ", "")))
                        .cancelOPAP(data.getCancelOPAP())
                        .build();
                registerRepository.save(register);
                break;
            }
        }
    }


    public List<BookScheduleInfo> getSchedule(Long studentId, LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다."));
        applyExceptionHandling(date, student);
        return sejongRegisterService.crawlBookScheduleInfo(student.getSessionToken(), date);
    }

    public void testCancel(Long studentId, Long registerId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다."));
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "존재하지 않는 예약입니다."));

        if (!register.getStudent().equals(student)) {
            throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
        }

        sejongRegisterService.cancelRegister(student.getSessionToken(), register.getCancelOPAP());
        registerRepository.delete(register);
    }


    public List<ResponseMyRegisterDto> getMyRegister(Long studentId) {
        List<Register> allByStudentId = registerRepository.findAllByStudentId(studentId);
        return allByStudentId.stream().map(ResponseMyRegisterDto::new).collect(Collectors.toList());
    }

    public String isAvailableBook(Long studentId, String title) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다."));
        Book book = bookRepository.findByTitle(title).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서를 찾을 수 없습니다."));
        List<StudentBookInfo> bookList = sejongRegisterService.crawlStudentBookInfo(student.getSessionToken(), book.getField().getCode());

        StudentBookInfo findBook = bookList.stream().filter(bookCodeInfo -> bookCodeInfo.getTitle().equals(title)).findAny()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서를 찾을 수 없습니다."));
        log.info("appCount: {}", findBook.getAppCount());
        if(findBook.getAppCount() < 2){
            return "true";
        }
        return "false";
    }

    private void applyExceptionHandling(LocalDate date, Student student) {
        //이전 날짜 신청 시도
        if (date.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_DATE, "신청하려는 날짜가 이미 지난 날짜입니다.");
        }
        //예약 가능한 학기가 지난 사용자가 신청 시도
        if (!student.getSemester().isAvailableSemester()) {
            throw new CustomException(ErrorCode.UNAVAILABLE_SEMESTER, "인증 시험 신청 가능 학기가 아닙니다.");
        }
        //한 주에 여러 건 신청할 경우
        if (isExceedApply(student, date)) {
            throw new CustomException(ErrorCode.EXCEED_APPLY, "현재 한 주에 1회 시험 신청이 가능합니다");
        }
        //한 달보다 뒤의 날짜 신청 시도
        if (date.isAfter(LocalDate.now().plusMonths(1))) {
            throw new CustomException(ErrorCode.INVALID_DATE, "1달 이내의 시험만 신청할 수 있습니다.");
        }
    }

    //1주일에 2건 이상 신청 시도시
    private boolean isExceedApply(Student student, LocalDate date) {
        LocalDate startDate = date.with(DayOfWeek.MONDAY);
        LocalDate endDate = date.with(DayOfWeek.SUNDAY);
        return registerRepository.existsByStudentAndDateBetween(student, startDate, endDate);
    }
}

