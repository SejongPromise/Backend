package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestSignupDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import java.util.Optional;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final SejongClassicAuthenticationService sejongClassicAuthenticationService;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;

    /**
     * 유저 id, pw 로 대양 휴머니티 칼리지 인증 접속을 합니다.
     * 인증 후, 마이페이지에서 학번, 전공, 이름, 학기, 인증여부를 포함한 Student 를 생성하고 저장합니다.
     * 기존 Student 가 있을 경우, 변경감지를 통해 Update합니다.
     * @param dto 학번, 학과
     * @return 학번, 전공, 이름, 학기
     */

    @Transactional
    public ResponseStudentInfoDto save(RequestSignupDto dto){
        SejongAuth auth = sejongClassicAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        ClassicStudentInfo studentInfo = sejongClassicCrawlerService.getStudentInfo(auth);
        Student student = Student.builder()
                .name(studentInfo.getName())
                .major(studentInfo.getMajor())
                .studentId(Long.parseLong(studentInfo.getStudentId()))
                .semester(Integer.parseInt(studentInfo.getSemester().substring(0,1)))
                .sessionToken(WebUtil.makeCookieString(auth.cookies))
                .pass(studentInfo.isPass())
                .build();
        Optional<Student> optionalStudent = studentRepository.findById(Long.parseLong(dto.getStudentId()));
        if(optionalStudent.isEmpty()){
            studentRepository.save(student);
        }else{
            Student existStudent = optionalStudent.get();
            existStudent.update(student);
        }
        return new ResponseStudentInfoDto(student.getId(), student.getMajor(), student.getName(), student.getSemester());
    }
}
