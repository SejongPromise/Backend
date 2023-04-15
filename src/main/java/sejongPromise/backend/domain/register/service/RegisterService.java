package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongBookService;
import sejongPromise.backend.infra.sejong.service.classic.SejongRegisterService;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final SejongRegisterService registerService;
    private final SejongBookService bookService;

    /**
     * 시험 신청 서비스
     * @param studentId
     * @param dto
     */
    public void applyTest(Long studentId, RequestCreateRegisterDto dto){
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저가 존재하지 않습니다."));
        Book book = bookRepository.findByTitle(dto.getBookTitle()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서를 찾을 수 없습니다."));
        // 책코드 찾아오기.
        Long bookCode = bookService.findBookCode(student.getSessionToken(), book.getTitle(), book.getField().getCode().toString());
        // 신청 DTO 생성.
        RequestTestApplyDto requestTestApplyDto = RequestTestApplyDto.builder()
                .bkAreaCode(book.getField().getCode().toString())
                .bkCode(bookCode.toString())
                .shInfoId(dto.getShInfoId())
                .build();

        registerService.registerTest(student.getSessionToken(), requestTestApplyDto);
        log.info("시험 예약 완료");
        // todo : 시험 신청을 하면 OPAP 값을 던져주어야 한다.
        List<MyRegisterInfo> myRegisterInfos = registerService.crawlRegisterInfo(student.getSessionToken());
        myRegisterInfos.forEach(data ->{
            if(data.getBookTitle().equals(dto.getBookTitle()) && !data.getCancelOPAP().isBlank()){
                //register 생성
                Register register = Register.builder()
                        .bookTitle(dto.getBookTitle())
                        .date(dto.getDate())
                        .startTime(dto.getTime())
                        .endTime(dto.getTime().plusMinutes(30L))
                        .year(dto.getYear())
                        .student(student)
                        .semester(Semester.of(dto.getSemester()))
                        .status(RegisterStatus.ACTIVE)
                        .cancelOPAP(data.getCancelOPAP())
                        .build();
                registerRepository.save(register);
            }
        });
    }


    public List<BookScheduleInfo> getSchedule(Long studentId, LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다."));
        return registerService.crawlBookScheduleInfo(student.getSessionToken(), date);
    }


    public void testCancel(Long studentId, Long registerId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Register register = registerRepository.findById(registerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        if (!register.getStudent().equals(student)) {
            throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
        }
        registerService.cancelRegister(student.getSessionToken(), register.getCancelOPAP());
        log.info("예약 취소 완료");
        register.cancelRegister();
    }


}

