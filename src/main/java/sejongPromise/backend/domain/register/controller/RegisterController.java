package sejongPromise.backend.domain.register.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.register.model.dto.request.RequestCancelRegisterDto;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.domain.register.model.dto.response.ResponseMyRegisterDto;
import sejongPromise.backend.domain.register.service.RegisterService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.jwt.JwtProvider;
import sejongPromise.backend.global.config.qualifier.Student;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "시험 예약 API", description = "시험 예약 API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    /**
     * 나의 시험 신청 현황을 조회합니다.
     * @param auth
     * @return 나의 신청시험 현황
     */
    @GetMapping
    @Student
    public List<ResponseMyRegisterDto> getMyRegister(CustomAuthentication auth) {
        Long studentId = auth.getStudentId();
        return registerService.getMyRegister(studentId);
    }

    /**
     * 시험 예약 API
     * @param dto 시험 예약 정보 일자, 시간, 년도, 학기, 제목, 신청버튼 값
     */
    @PostMapping("/apply")
    @Student
    public void testApply(CustomAuthentication auth, @RequestBody RequestCreateRegisterDto dto) {
        Long studentId = auth.getStudentId();
        registerService.applyTest(studentId, dto);
    }

    /**
     * 시험 일정 API - 일자의 시험 정보를 가져옵니다, 시험신청 버튼 값을 활용해야 합니다.
     * @param date 요청파마리터 yyyy-mm-dd 형식
     * @return
     */
    @GetMapping("/schedule")
    @Student
    public List<BookScheduleInfo> getTestSchedule(CustomAuthentication auth,
                                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long studentId = auth.getStudentId();
        return registerService.getSchedule(studentId, date);
    }

    /**
     * 시험 예약 취소 API
     * @param registerId 예약 id
     */
    @DeleteMapping("/{id}")
    @Student
    public void testRegisterCancel(CustomAuthentication auth,
                                @PathVariable("id") Long registerId) {
        Long studentId = auth.getStudentId();
        registerService.testCancel(studentId, registerId);
    }

}
