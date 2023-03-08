package sejongPromise.backend.debug;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.StudentInfo;
import sejongPromise.backend.infra.sejong.service.portal.SejongAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;
import sejongPromise.backend.infra.sejong.service.portal.SejongCrawlerService;

@Tag(name = "테스트용 컨트롤러", description = "개발하면서 필요한 debug용 Controller")
@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final SejongAuthenticationService authenticationService;
    private final SejongClassicAuthenticationService classicAuthenticationService;
    private final SejongClassicCrawlerService classicCrawlerService;
    private final SejongCrawlerService crawlerService;

    /**
     * 세종대학교 포털 로그인
     * @param dto 학번 & 비밀번호
     * @return
     */
    @GetMapping("/auth")
    public StudentInfo ssoToken(@RequestBody requestDto dto){
        SejongAuth login = authenticationService.login(dto.getStudentId(), dto.getPassword());
        StudentInfo studentInfo = crawlerService.crawlStudentInfo(login);
        printData(studentInfo);
        return studentInfo;
    }



    @GetMapping("/classic/auth")
    public String classicInfo(){
        SejongAuth login = classicAuthenticationService.login("학번", "비밀번호");
        String s = classicCrawlerService.crawlStudentCertificationInfo(login);
        return s;
    }

    private static void printData(StudentInfo studentInfo) {
        String studentName = studentInfo.getStudentName();
        System.out.println("studentName = " + studentName);
        String studentId = studentInfo.getStudentId();
        System.out.println("studentId = " + studentId);
        String major = studentInfo.getMajor();
        System.out.println("major = " + major);
    }

    @Getter
    @RequiredArgsConstructor
    public class requestDto{
        private final String studentId;
        private final String password;

    }
}
