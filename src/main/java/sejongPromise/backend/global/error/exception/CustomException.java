package sejongPromise.backend.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sejongPromise.backend.global.error.ErrorCode;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    ErrorCode errorCode;

    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
