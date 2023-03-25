package sejongPromise.backend.global.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider tokenProvider;

    //todo: 시큐리티 에러 핸들링
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //header 에서 token 추출
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String accessToken = tokenProvider.resolveToken(httpServletRequest);

        if (accessToken != null) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);//customAuthentication 반환
            SecurityContextHolder.getContext().setAuthentication(authentication); //securityContext 에 authentication 객체 저장
        }

        chain.doFilter(request, response);
    }
}
