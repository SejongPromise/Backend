package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.domain.register.model.dto.request.RequestCancelRegisterDto;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.dto.GetCancelDataDto;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerServiceTwo;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;
    private final StudentRepository studentRepository;
    private final SejongClassicCrawlerServiceTwo sejongClassicCrawlerServiceTwo;

    //todo : 응시가 완료된 시점에서 스케줄링으로 상태값 반환하기
    public void cancelRegister(Register register){
        //todo : 이부분 어짜피 testCancel 에서 Register 조회하니까 Register 인자로 받게 수정했습니다.
        // Register의 Student match 여부를 testCancel에서 조회하도록 옮겼습니다
//        Register register = registerRepository.findById(registerId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 시험을 찾을 수 없습니다.")); //databases에 registerId 존재
//        log.info("studentId: {}, register.getStuedentId: {}", studentId, register.getStudent().getId());
//        if(studentId.equals(register.getStudent().getId())){
            register.cancelRegister(); //캔슬 호출
            registerRepository.save(register); //다시 저장
//        }
//        throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
    }

    //신청하기->가영이가 신청하기 로직 만들면 신청하자마자 CREATEREGISTER 로직 구현하기
    // -> 신청하자마자는 신청하면서 바로 넣었는데 크롤링해서 넣어야 하면 Register 생성 부분 없애고 수정해주세요!

    /**
     * 시험 신청 서비스
     * @param studentId
     * @param dto
     */
    public void testApply(Long studentId, RequestCreateRegisterDto dto){
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        //시험 예약
        RequestTestApplyDto requestTestApplyDto = RequestTestApplyDto.builder()
                .bkAreaCode(dto.getBookAreaCode())
                .bkCode(dto.getBookCode())
                .shInfoId(dto.getShInfoId())
                .build();
        sejongClassicCrawlerService.testRegister(student.getSessionToken(), requestTestApplyDto);

        log.info("시험 예약 완료");

        //register 생성
        createRegister(student, dto);
    }

    /**
     * Register 생성 서비스
     * @param student
     * @param dto
     */
    public void createRegister(Student student, RequestCreateRegisterDto dto) {
        String cancelData = sejongClassicCrawlerServiceTwo.getCancelOPAP(student.getSessionToken(),
                new GetCancelDataDto(dto.getDate()));

        log.info("cancleData: {}", cancelData);
        //Register 생성
        Register register = Register.builder()
                .bookTitle(dto.getBookTitle())
                .date(dto.getDate())
                .startTime(dto.getTime())
                .endTime(dto.getTime().plusMinutes(30L))
                .year(dto.getDate().getYear())
                .student(student)
                .semester(dto.getSemester())
                .status(RegisterStatus.ACTIVE)
                .cancelData(cancelData)
                .build();

        registerRepository.save(register);
    }

    public List<BookScheduleInfo> getSchedule(long studentId, LocalDate date) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        log.info("jsession: {}", student.getSessionToken());
        return sejongClassicCrawlerService.getScheduleInfo(student.getSessionToken(), String.valueOf(date));
    }

    public long getBookCode(long studentId, RequestFindBookCodeDto dto) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        return sejongClassicCrawlerService.findBookCode(student.getSessionToken(), dto);
    }

    public void testCancel(long studentId, RequestCancelRegisterDto dto) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Register register = registerRepository.findById(dto.getRegisterId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        if (!register.getStudent().equals(student)) { //학번 비교할지 student 객체 비교할지 고민하다가 객체 비교로 구현했습니다.
            throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
        }
        sejongClassicCrawlerServiceTwo.cancelRegister(student.getSessionToken(), register.getCancelData());
        log.info("예약 취소 완료");
        cancelRegister(register);
    }


}

