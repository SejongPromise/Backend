package sejongPromise.backend.domain.register.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
    public void cancelRegister(Authentication auth, @PathVariable Long registerId){
        Long studentId = (Long)auth.getPrincipal();
        registerService.cancelRegister(studentId, registerId);

    }

}
