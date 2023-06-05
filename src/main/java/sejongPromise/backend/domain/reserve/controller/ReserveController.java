package sejongPromise.backend.domain.reserve.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.reserve.model.dto.response.ResponseReserveDto;
import sejongPromise.backend.domain.reserve.service.ReserveService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.qualifier.StudentAuth;


import java.util.List;

@Tag(name = "도서 예약 API", description = "도서 예약 API 모음")
@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    /**
     * 나의 도서 예약 현황을 조회합니다.
     *
     * @param auth
     * @return 나의 도서 예약 현황을 조회합니다.
     */
    @GetMapping
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
    @GetMapping("/book")
    @StudentAuth
    public String checkStatus(@RequestParam("title") String title) {
        return reserveService.checkStatus(title);
    }

    /**
     * 도서 예약 API
     * @param title 도서 제목
     */
    @PostMapping
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
    @DeleteMapping("/{id}")
    @StudentAuth
    public void testRegisterCancel(CustomAuthentication auth,
                                   @PathVariable("id") Long reserveId) {
        Long studentId = auth.getStudentId();
        reserveService.cancel(studentId, reserveId);
    }

}