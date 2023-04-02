package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.repository.ExamRepository;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
import sejongPromise.backend.infra.sejong.model.ExamInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignupService {
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final RegisterRepository registerRepository;
    private final SejongClassicAuthenticationService sejongClassicAuthenticationService;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 유저 id, pw 로 대양 휴머니티 칼리지 인증 접속을 합니다.
     * 인증 후, 마이페이지에서 학번, 전공, 이름, 학기, 인증여부를 포함한 Student 를 생성하고 저장합니다.
     * 인증을 한번이라도 한 학생은 업데이트가 불가합니다.
     * @param dto 학번, 학과
     * @return 학번, 전공, 이름, 학기
     */
    @Transactional
    public void signup(RequestStudentInfoDto dto){
        //todo: 비밀번호 변경 가능하게 할지 말지
        Optional<Student> optionalStudent = studentRepository.findById(Long.parseLong(dto.getStudentId()));
        if(optionalStudent.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_USER_EXIST);
        }

        //1. 학생 정보 저장
        SejongAuth auth = sejongClassicAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        ClassicStudentInfo studentInfo = sejongClassicCrawlerService.getStudentInfo(auth);
        Student student = Student.builder()
                .name(studentInfo.getName())
                .major(studentInfo.getMajor())
                .studentId(Long.parseLong(studentInfo.getStudentId()))
                .semester(studentInfo.getSemester().replace(" ", ""))
                .sessionToken(WebUtil.makeCookieString(auth.cookies))
                .pass(studentInfo.isPass())
                .encodedPassword(passwordEncoder.encode(dto.getPassword()))
                .build();
        Student saveStudent = studentRepository.save(student);

        //2.시험 정보 저장
        List<ExamInfo> examInfoList = studentInfo.getExamInfoList();
        examInfoList.stream().distinct().forEach(data -> {
            Exam exam = Exam.builder().title(data.getTitle())
                    .isPass(data.isPass())
                    .field(data.getField())
                    .year(data.getYear())
                    .semester(data.getSemester())
                    .student(saveStudent)
                    .build();
            examRepository.save(exam);
        });

        //3.신청내역과 신청 취소 내역 저장
        List<MyRegisterInfo> myRegisterInfoList = sejongClassicCrawlerService.getMyRegisterInfo(auth);
        myRegisterInfoList.forEach(data -> {
            LocalDateTime deleteDate = null;
            //todo : 승환님께서 마음에 안 든다고,,ㅎㅎ
            if(data.getDeleteDate() != null){
                deleteDate = LocalDateTime.parse(data.getDeleteDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            Register register = Register.builder()
                    .student(saveStudent)
                    .year(Integer.parseInt(data.getYear()))
                    .semester(Semester.of(data.getSemester()))
                    .date(LocalDate.parse(data.getDate(), DateTimeFormatter.ISO_DATE))
                    .startTime(LocalTime.parse(data.getStartTime(), DateTimeFormatter.ISO_TIME))
                    .endTime(LocalTime.parse(data.getEndTime(), DateTimeFormatter.ISO_TIME))
                    .bookTitle(data.getBookTitle())
                    .status(data.getIsCancel() ? RegisterStatus.CANCELED : RegisterStatus.ACTIVE)
                    .deleteDate(deleteDate)
                    .build();
            registerRepository.save(register);

        });

    }


}
