package sejongPromise.backend.domain.register.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.register.model.dto.request.RequestCreateRegisterDto;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.domain.register.service.RegisterService;
import sejongPromise.backend.global.config.jwt.JwtProvider;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "시험 예약 API", description = "시험 예약 API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/cancel/{registerId}")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public void cancelRegister(Authentication auth, @PathVariable Long registerId) {
        Long studentId = (Long) auth.getPrincipal();
        registerService.cancelRegister(studentId, registerId);
    }

    @Operation(summary = "Register 생성, 시험 예약", description = "RegisterCreateRequestDto를 이용해 register를 생성합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "register 생성 성공")
    })
    @PostMapping("/")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public void testApply(Authentication auth, @RequestBody RequestCreateRegisterDto dto) {
        Long studentId = (Long) auth.getPrincipal();
        registerService.testApply(studentId, dto);
    }

    @Operation(summary = "시험 스케쥴 가져오기", description = "해당 일자의 시험 정보를 가져옵니다.", responses = {
            @ApiResponse(responseCode = "200", description = "시험 정보 가져오기 완료")
    })
    @GetMapping("/schedule")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public List<BookScheduleInfo> getTestSchedule(Authentication auth,
                                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long studentId = (Long) auth.getPrincipal();
        return registerService.getSchedule(studentId, date);
    }

    @Operation(summary = "책 코드 가져오기", description = "제목과 영역 코드로 책 코드를 가져옵니다.", responses = {
            @ApiResponse(responseCode = "200", description = "책 코드 정보 가져오기 완료")
    })
    @GetMapping("/bookcode")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public long getTestSchedule(Authentication auth,
                                                  @RequestParam("bookAreaCode") String bookAreaCode,
                                                  @RequestParam("bookTitle") String bookTitle) {
        Long studentId = (Long) auth.getPrincipal();
        return registerService.getBookCode(studentId, new RequestFindBookCodeDto(bookAreaCode, bookTitle));
    }
}
