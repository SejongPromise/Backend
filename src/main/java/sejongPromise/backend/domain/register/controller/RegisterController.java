package sejongPromise.backend.domain.register.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.register.model.dto.request.RegisterTestApplyRequestDto;
import sejongPromise.backend.domain.register.service.RegisterService;
import sejongPromise.backend.global.config.jwt.JwtProvider;

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
    @PostMapping("/test")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public void testApply(Authentication auth, @RequestBody RegisterTestApplyRequestDto dto) {
        Long studentId = (Long) auth.getPrincipal();
        registerService.testRegister(studentId, dto);
    }
}
