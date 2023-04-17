package sejongPromise.backend.global.config.auth;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        throw new CustomException(ErrorCode.NOT_FOUND_DATA);
    }
}
