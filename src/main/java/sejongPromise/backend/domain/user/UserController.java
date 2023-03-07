package sejongPromise.backend.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity join(@Valid @RequestBody UserRequestDto userRequestDto) {
        userService.join(userRequestDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserRequestDto userRequestDto){
        String token = userService.login(userRequestDto);
        if (token.isEmpty()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(token, HttpStatus.OK);

    }
}
