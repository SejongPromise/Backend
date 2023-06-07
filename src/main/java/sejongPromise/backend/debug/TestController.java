package sejongPromise.backend.debug;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import sejongPromise.backend.global.config.qualifier.StudentAuth;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.*;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
import sejongPromise.backend.debug.dto.TestLoginDto;
import sejongPromise.backend.debug.dto.RequestTestCancelDto;
import sejongPromise.backend.infra.sejong.service.classic.SejongAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongBookService;
import sejongPromise.backend.infra.sejong.service.classic.SejongRegisterService;
import sejongPromise.backend.infra.sejong.service.portal.ReservingBookService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "테스트용 컨트롤러", description = "개발하면서 필요한 debug용 Controller")
@RestController
@RequestMapping("/test")
@Slf4j
@RequiredArgsConstructor
public class TestController {
    private final SejongAuthenticationService classicAuthenticationService;
    private final SejongRegisterService registerService;
    private final SejongBookService bookService;
    private final ReserveService reserveService;

    private final ReservingBookService reservingBookService;


    /**
     * @param date ("yyyy-mm-dd" 형식)
     * @return 스케쥴 List
     */
    @GetMapping("/classic/schedule")
    public List<BookScheduleInfo> classicSchedule(@RequestParam("date") LocalDate date, @RequestParam("JSession") String JSession) {
        return registerService.crawlBookScheduleInfo(JSession, date);
    }

    @GetMapping("/auth/student")
    public Long getAuth(Authentication auth) {
        Long studentId = (Long) auth.getPrincipal();
        return studentId;
    }

    @GetMapping("/auth/student/schedule")
    public List<MyRegisterInfo> getSchedule(@RequestBody TestLoginDto dto) {
        SejongAuth auth = classicAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        return registerService.crawlRegisterInfo(WebUtil.makeCookieString(auth.cookies));
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
     * @return 책 코드값
     */
    @GetMapping("/classic/book")
    public List<BookCodeInfo> classicBookCode(@RequestParam("areaCode") String areaCode) {

        return bookService.crawlBookCode(areaCode);
    }

    public void classicRegisterCancel(@RequestParam("JSession") String JSession,
                                      @RequestBody RequestTestCancelDto dto) {
        registerService.cancelRegister(JSession, dto.getCancelData());
    }

    /**
     * 나의 도서 예약 현황을 조회합니다.
     *
     * @param auth
     * @return 나의 도서 예약 현황을 조회합니다.
     */
    @GetMapping("/reserve/info")
    @StudentAuth
    public List<ResponseReserveDto> getMyReserveList(CustomAuthentication auth) {
        Long studentId = auth.getStudentId();
        return reserveService.list(studentId);
    }

    /**
     * 도서 예약 여부 확인 API
     *
     * @return 도서 예약 여부 : 대출가능 | 대출불가능 | 예약가능
     */
    @GetMapping("/reserve")
    public String checkStatus(@RequestParam("title") String title) {
        return reserveService.checkStatus(title);
    }

    /**
     * 도서 예약 API
     * @param title 도서 제목
     */
    @PostMapping("/reserve")
    @StudentAuth
    public void getTestSchedule(CustomAuthentication auth,
                                @RequestParam("title") String title){
        Long studentId = auth.getStudentId();
        reserveService.reserve(studentId, title);
    }

    /**
     * 도서 예약 취소 API
     *
     * @param reserveId 예약 id
     */
    @DeleteMapping("/reserve/{id}")
    @StudentAuth
    public void testRegisterCancel(CustomAuthentication auth,
                                   @PathVariable("id") Long reserveId) {
        Long studentId = auth.getStudentId();
        reserveService.cancel(studentId, reserveId);
    }
}
