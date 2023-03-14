package sejongPromise.backend.global.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.domain.user.UserRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;
    @PostMapping("/token")
    public ResponseEntity getToken(@Valid @RequestBody UserRequestDto userRequestDto) {
        return jwtService.getJwtToken(userRequestDto.getStudentNum());
    }
}