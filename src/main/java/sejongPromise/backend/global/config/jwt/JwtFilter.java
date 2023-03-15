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
            log.info("token: {}",token);
            Authentication authentication = tokenProvider.getAuthentication(token); //customAuthentication 반환
            SecurityContextHolder.getContext().setAuthentication(authentication); //securityContext 에 authentication 객체 저장
            log.info("security context에 학번: {} 인증 정보 저장함, url:{}",authentication.getName(), ((HttpServletRequest) request).getRequestURI());
        }else{
            log.info("유효한 jwt 토큰 없음, url:{}",((HttpServletRequest) request).getRequestURI());
        }

        chain.doFilter(request, response);
    }
}
