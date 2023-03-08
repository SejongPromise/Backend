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
        if(!userService.join(userRequestDto)){
            return new ResponseEntity("이미 존재하는 학번",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody UserRequestDto userRequestDto){ //response 값 정리가 필요
        String token = userService.login(userRequestDto);
        if (token.isEmpty()) {
            return new ResponseEntity("아이디나 비번 정보가 틀림",HttpStatus.BAD_REQUEST);
        }
        if(token=="UsernameNotFoundException"){
            return new ResponseEntity("해당하는 회원 정보가 없음",HttpStatus.BAD_REQUEST);
        }
        else if(token == "BadCredentialsException"){
            return new ResponseEntity("비밀번호 정보가 일치하지 않음",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(token, HttpStatus.OK);

    }
}
