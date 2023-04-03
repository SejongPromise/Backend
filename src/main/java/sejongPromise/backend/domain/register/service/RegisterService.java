package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.register.model.dto.request.RegisterCreateRequestDto;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.service.StudentService;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.dto.request.TestBookScheduleRequestDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;
    private final StudentService studentService;

    //todo : 응시가 완료된 시점에서 스케줄링으로 상태값 반환하기
    public void cancelRegister(Long studentId, Long registerId){

        Register register = registerRepository.findById(registerId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 시험을 찾을 수 없습니다.")); //databases에 registerId 존재
        if(studentId.equals(register.getStudent().getId())){
            register.cancelRegister(); //캔슬 호출
            registerRepository.save(register); //다시 저장
        }
        throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
    }

    //신청하기->가영이가 신청하기 로직 만들면 신청하자마자 CREATEREGISTER 로직 구현하기
    // -> 신청하자마자는 신청하면서 바로 넣었는데 크롤링해서 넣어야 하면 Register 생성 부분 없애고 수정해주세요!

    /**
     * 시험 신청, Register 생성 서비스
     * @param studentId
     * @param dto
     */
    public void createRegister(Long studentId, RegisterCreateRequestDto dto) {
        //JSESSIION으로 쿠키 생성
        Student student = studentService.findStudentById(studentId);
        MultiValueMap<String, String> cookie = new LinkedMultiValueMap<>();
        String[] split = student.getSessionToken().split("=");
        cookie.add(split[0], split[1]);
        SejongAuth auth = new SejongAuth(cookie);

        //시험 예약
        TestBookScheduleRequestDto testBookScheduleRequestDto = TestBookScheduleRequestDto.builder()
                .bkAreaCode(dto.getBookAreaCode())
                .bkCode(dto.getBookCode())
                .shInfoId(dto.getShInfoId())
                .build();
        sejongClassicCrawlerService.testRegister(auth,testBookScheduleRequestDto);

        //Register 생성
        Register register = Register.builder()
                .bookTitle(dto.getBookTitle())
                .date(dto.getDate())
                .startTime(dto.getTime())
                .endTime(dto.getTime().plusMinutes(30L))
                .year(dto.getDate().getYear())
                .student(student)
                .semester(student.getSemester())
                .status(RegisterStatus.ACTIVE)
                .deleteDate(LocalDateTime.now()) //deleteDate는 builder에 있으면 안될 것 같아요. 일단 현재 시간 넣었습니다.
                .build();

        registerRepository.save(register);
    }

}

