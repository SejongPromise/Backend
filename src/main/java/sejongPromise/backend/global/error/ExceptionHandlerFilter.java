package sejongPromise.backend.global.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sejongPromise.backend.global.error.dto.ErrorResponseDto;
import sejongPromise.backend.global.error.exception.CustomException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (CustomException e){
            writeErrorResponse(response, e);
        }catch(Exception e){
            writeUnexpectedErrorResponse(response, e);
        }
    }
    private void writeErrorResponse(HttpServletResponse response, CustomException e) throws IOException {
        ErrorResponseDto dto = new ErrorResponseDto(e);
        log.error("A problem has occurred in filter: [id={}]", dto.getTrackingId(), e);
        writeResponse(response, dto, e.getErrorCode().getStatus().value());
    }
    private void writeUnexpectedErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        ErrorResponseDto dto = new ErrorResponseDto(e);
        log.error("Unexpected exception has occurred in filter: [id={}]", dto.getTrackingId(), e);
        writeResponse(response, dto, 500);
    }
    private void writeResponse(HttpServletResponse response, Object dto, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String exceptionMessage = objectMapper.writeValueAsString(dto);
        response.getWriter().write(exceptionMessage);
    }
}
