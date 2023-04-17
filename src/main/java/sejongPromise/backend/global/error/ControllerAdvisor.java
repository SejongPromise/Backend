package sejongPromise.backend.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sejongPromise.backend.global.error.dto.ErrorResponseDto;
import sejongPromise.backend.global.error.exception.CustomException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> customExceptionHandler(CustomException e){
        ErrorResponseDto dto = new ErrorResponseDto(e);
        log.error("Error occurred in controller advice: [id={}]", dto.getTrackingId());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(dto);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpMessageNotReadableException e) {
        CustomException customException = new CustomException(ErrorCode.INVALID_REQUEST);
        return customExceptionHandler(customException);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameterException(BindException e){
        ErrorResponseDto dto = new ErrorResponseDto(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameterException(MethodArgumentTypeMismatchException e){
        ErrorResponseDto dto = new ErrorResponseDto(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> accessDenied(AccessDeniedException e) {
        return customExceptionHandler(new CustomException(ErrorCode.NOT_GRANTED));
    }

//    @ExceptionHandler
//    protected ResponseEntity<ErrorResponseDto> badParameterException(Exception e){
//        ErrorResponseDto dto = new ErrorResponseDto(e);
//        log.error("UnExpected Error occurred in controller advice: [id={}]", dto.getTrackingId());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
//    }
}
