package sejongPromise.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sejongPromise.backend.global.config.auth.CustomAccessDeniedHandler;
import sejongPromise.backend.global.config.auth.CustomAuthenticationEntryPoint;
import sejongPromise.backend.global.config.jwt.JwtFilter;
import sejongPromise.backend.global.error.ExceptionHandlerFilter;

@EnableWebSecurity //security 필터 등록됨
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true) //secured 어노테이션 활성화
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private static final String[] PUBLIC_URI = {
            "/swagger-ui/**", "/api-docs/**", "/", "/img/**", "/lib/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(PUBLIC_URI).permitAll()
                /**세션 사용 안함*/
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                /**jwt 설정*/
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint); //jwt filter 추가 (커스텀 필터, 그 후에 등록될 필터)

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
