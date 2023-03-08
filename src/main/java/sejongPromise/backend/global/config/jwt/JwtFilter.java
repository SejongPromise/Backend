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
        String token = tokenProvider.resolveToken((HttpServletRequest) request);

        if (token != null && tokenProvider.isValidateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token); //token 에서 user 정보 받아옴
            SecurityContextHolder.getContext().setAuthentication(authentication); //securityContext에 authentication 객체 저장
<<<<<<< Updated upstream
            log.info("security context에 '{}' 인증 정보 저장함, url:{}", authentication.getName(), ((HttpServletRequest) request).getRequestURI());
=======
            log.info("security context에 학번: {} 인증 정보 저장함, url:{}",authentication.getName(), ((HttpServletRequest) request).getRequestURI());
>>>>>>> Stashed changes
        }else{
            log.info("유효한 jwt 토큰 없음, url:{}",((HttpServletRequest) request).getRequestURI());
        }

        chain.doFilter(request, response);
    }
}
