package sejongPromise.backend.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sejongPromise.backend.global.error.dto.ErrorResponseDto;
import sejongPromise.backend.global.error.exception.CustomException;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> customExceptionHandler(CustomException e){
        ErrorResponseDto dto = new ErrorResponseDto(e);
        log.error("Error occurred in controller advice: [id={}]", dto.getTrackingId());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(dto);
    }

}
