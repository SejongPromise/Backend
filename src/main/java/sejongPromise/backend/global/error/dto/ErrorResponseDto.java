package sejongPromise.backend.global.error.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sejongPromise.backend.global.error.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ErrorResponseDto {
    private final String timestamp;
    private final String trackingId;
    private final HttpStatus status;
    private final String code;
    private final List<Object> message;
    private final String detailMessage;

    public ErrorResponseDto(CustomException e){
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = e.getErrorCode().getStatus();
        this.code = e.getClass().getSimpleName();
        this.message = List.of(e.getErrorCode().getMessage());
        this.detailMessage = e.getMessage();
    }

    public ErrorResponseDto(BindException e){
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = HttpStatus.BAD_REQUEST;
        this.code = e.getClass().getSimpleName();
        this.message = e.getFieldErrors().stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage()).collect(Collectors.toList());
        this.detailMessage = "형식 오류";
    }


    public ErrorResponseDto(MethodArgumentTypeMismatchException e) {
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = HttpStatus.BAD_REQUEST;
        this.code = e.getClass().getSimpleName();
        this.message = List.of("잘못된 요청입니다.");
        this.detailMessage = "메소드 타입 오류";
    }

    public ErrorResponseDto(Exception e) {
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = e.getClass().getSimpleName();
        this.message = List.of("서버 에러");
        this.detailMessage = e.getMessage();
    }
}
