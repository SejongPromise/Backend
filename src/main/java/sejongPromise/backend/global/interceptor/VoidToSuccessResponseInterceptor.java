package sejongPromise.backend.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sejongPromise.backend.global.model.ResponseSuccessDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class VoidToSuccessResponseInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        if (httpStatus.is2xxSuccessful() && response.getContentType() == null) {
            String wrappedBody = objectMapper.writeValueAsString(new ResponseSuccessDto());
            byte[] bytes = wrappedBody.getBytes();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.resetBuffer();
            response.getOutputStream().write(bytes, 0, bytes.length);
        }
    }
}
