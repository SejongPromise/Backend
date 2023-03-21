package sejongPromise.backend.global.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //header 에서 token 추출
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = tokenProvider.resolveToken(httpServletRequest);

        if (token != null && tokenProvider.isValidateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);//customAuthentication 반환
            SecurityContextHolder.getContext().setAuthentication(authentication); //securityContext 에 authentication 객체 저장
        }
        chain.doFilter(request, response);
    }
}
