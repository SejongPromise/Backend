package sejongPromise.backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
    NO_RESPONSE(HttpStatus.NOT_FOUND, "응답값이 존재하지 않습니다."),
    INVALID_RESPONSE(HttpStatus.BAD_REQUEST, "유효하지 않은 응답입니다."),
    ALREADY_USER_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다.");

    private final HttpStatus status;
    private final String message;

}
