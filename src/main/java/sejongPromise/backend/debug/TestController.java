package sejongPromise.backend.debug;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.infra.sejong.model.dto.GetCancelDataDto;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.debug.dto.TestLoginDto;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.PortalStudentInfo;
import sejongPromise.backend.debug.dto.RequestTestCancelDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerServiceTwo;
import sejongPromise.backend.infra.sejong.service.portal.SejongAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;
import sejongPromise.backend.infra.sejong.service.portal.SejongCrawlerService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


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
    private final SejongClassicCrawlerServiceTwo sejongClassicCrawlerServiceTwo;

    /**
     * 세종대학교 포털 로그인
     *
     * @param dto 학번 & 비밀번호
     * @return
     */
    @GetMapping("/auth")
    public PortalStudentInfo ssoToken(@RequestBody TestLoginDto dto) {
        SejongAuth login = authenticationService.login(dto.getStudentId(), dto.getPassword());
        PortalStudentInfo portalStudentInfo = crawlerService.crawlStudentInfo(login);
        printData(portalStudentInfo);
        return portalStudentInfo;
    }


    /**
     * @param date ("yyyy-mm-dd" 형식)
     * @return 스케쥴 List
     */
    @GetMapping("/classic/schedule")
    public List<BookScheduleInfo> classicSchedule(@RequestParam("date") String date, @RequestParam("JSession") String JSession) {
        return classicCrawlerService.getScheduleInfo(JSession, date);
    }

    @GetMapping("/auth/student")
    public Long getAuth(Authentication auth) {
        Long studentId = (Long) auth.getPrincipal();
        return studentId;
    }

    @GetMapping("/auth/student/schedule")
    public List<MyRegisterInfo> getSchedule(@RequestBody TestLoginDto dto) {
        SejongAuth auth = classicAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        return classicCrawlerService.getMyRegisterInfo(auth);
    }

    /**
     * 고전독서 인증 시험 신청
     *
     * @param dto shInfoId, opTermId, bkAreaCode, bkCode
     */
    //todo : 해당 부분도 위와 마찬가지 입니다.
    @PostMapping("/classic/test")
    public void classicTestRegister(@RequestBody RequestTestApplyDto dto, String id, String password) {
        SejongAuth login = classicAuthenticationService.login(id, password);
//        classicCrawlerService.testRegister(login, dto);
    }

    /**
     * 고전독서 인증 시험 신청에 필요한 책 code 찾기
     *
     * @param areaCode 분야
     * @param title    책 제목
     * @return 책 코드값
     */
    @GetMapping("/classic/book")
    public long classicBookCode(@RequestParam("title") String title,
                                @RequestParam("JSession") String JSession,
                                @RequestParam("areaCode") String areaCode) {

        return classicCrawlerService.findBookCode(JSession, new RequestFindBookCodeDto(areaCode, title));
    }


    private static void printData(PortalStudentInfo portalStudentInfo) {
        String studentName = portalStudentInfo.getStudentName();
        System.out.println("studentName = " + studentName);
        String studentId = portalStudentInfo.getStudentId();
        System.out.println("studentId = " + studentId);
        String major = portalStudentInfo.getMajor();
        System.out.println("major = " + major);
    }

    @GetMapping("classic/book/cancel")
    public String getClassicCancelOPAP(@RequestParam("JSession") String JSession,
                                       @RequestParam("title") String title,
                                       @RequestParam("date") LocalDate date,
                                       @RequestParam("startTime") LocalTime startTime) {
        return sejongClassicCrawlerServiceTwo.getCancelOPAP(JSession, new GetCancelDataDto(date));
    }

    @PostMapping("classic/book/cancel")
    public void classicRegisterCancel(@RequestParam("JSession") String JSession,
                                      @RequestBody RequestTestCancelDto dto) {
        sejongClassicCrawlerServiceTwo.cancelRegister(JSession, dto.getCancelData());
    }
}
