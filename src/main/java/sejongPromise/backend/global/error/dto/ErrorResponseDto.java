package sejongPromise.backend.global.error.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import sejongPromise.backend.global.error.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
        this.code = e.getErrorCode().toString();
        this.message = List.of(e.getErrorCode().getMessage());
        this.detailMessage = e.getMessage();
    }

}
