package sejongPromise.backend.debug.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.debug.user.UserService;
import sejongPromise.backend.debug.user.dto.request.UserRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // Todo : void 는 성공 응답 요청으로 바꿀 거임.
    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody UserRequestDto dto) {
        userService.join(dto);
        return ResponseEntity.ok("저장");
    }
}
