package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.repository.ExamRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
import sejongPromise.backend.infra.sejong.model.ExamInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignupService {
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final SejongClassicAuthenticationService sejongClassicAuthenticationService;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
        SejongAuth auth = sejongClassicAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        ClassicStudentInfo studentInfo = sejongClassicCrawlerService.getStudentInfo(auth);
        Student student = Student.builder()
                .name(studentInfo.getName())
                .major(studentInfo.getMajor())
                .studentId(Long.parseLong(studentInfo.getStudentId()))
                .semester(Integer.parseInt(studentInfo.getSemester().substring(0, 1)))
                .sessionToken(WebUtil.makeCookieString(auth.cookies))
                .pass(studentInfo.isPass())
                .encodedPassword(passwordEncoder.encode(dto.getPassword()))
                .build();
        Student saveStudent = studentRepository.save(student);

        List<ExamInfo> examInfoList = studentInfo.getExamInfoList();
        examInfoList.forEach(data -> {
            Exam build = Exam.builder().title(data.getTitle())
                    .isPass(data.isPass())
                    .field(data.getField())
                    .passAt(data.getPassAt())
                    .student(saveStudent)
                    .build();
            examRepository.save(build);
        });
    }
}
