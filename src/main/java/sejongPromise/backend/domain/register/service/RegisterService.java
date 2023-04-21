package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.register.model.dto.response.ResponseMyRegisterDto;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongRegisterService;

import javax.transaction.Transactional;
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

    /**
     * 시험 신청 서비스
     * @param studentId
     * @param dto
     */
    public void applyTest(Long studentId, RequestCreateRegisterDto dto){
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저가 존재하지 않습니다."));
        Book book = bookRepository.findByTitle(dto.getBookTitle()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서를 찾을 수 없습니다."));
        // 신청 DTO 생성.
        RequestTestApplyDto requestTestApplyDto = RequestTestApplyDto.builder()
                .bkAreaCode(book.getField().getCode().toString())
                .bkCode(book.getCode().toString())
                .shInfoId(dto.getShInfoId())
                .build();

        sejongRegisterService.registerTest(student.getSessionToken(), requestTestApplyDto);
        log.info("시험 예약 완료");
        // todo : 시험 신청을 하면 OPAP 값을 던져주어야 한다.
        List<MyRegisterInfo> myRegisterInfos = sejongRegisterService.crawlRegisterInfo(student.getSessionToken());
        // 시험 신청은 1일 1회이므로 date 로 구별한다.
        for (MyRegisterInfo data : myRegisterInfos) {
            if (data.getDate().equals(dto.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !data.getCancelOPAP().isBlank()) {
                //register 생성
                Register register = Register.builder()
                        .bookTitle(dto.getBookTitle()) // todo : BOOK 맵핑
                        .date(dto.getDate())
                        .startTime(dto.getTime())
                        .endTime(dto.getTime().plusMinutes(30L))
                        .year(dto.getYear())
                        .student(student)
                        .semester(Semester.of(dto.getSemester()))
                        .cancelOPAP(data.getCancelOPAP())
                        .build();
                registerRepository.save(register);
                break;
            }
        }
    }


    public List<BookScheduleInfo> getSchedule(Long studentId, LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다."));
        return sejongRegisterService.crawlBookScheduleInfo(student.getSessionToken(), date);
    }


    public void testCancel(Long studentId, Long registerId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

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
}

