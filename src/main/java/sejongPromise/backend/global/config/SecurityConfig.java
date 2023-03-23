package sejongPromise.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sejongPromise.backend.global.config.jwt.JwtFilter;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;

@EnableWebSecurity //security 필터 등록됨
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String[] PUBLIC_URI = {
            "/swagger-ui/**", "/api-docs/**", "/", "/img/**", "/lib/**"
    };
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .formLogin().disable()
                .httpBasic().disable()
                /**exception 핸들링 추가해야 함*/
                /**HttpServletRequest 사용하는 요청에 대한 접근 제한 설정 (사용권한 체크)*/
                .authorizeRequests()
                .antMatchers("/join**","/token**").permitAll()
                .antMatchers(PUBLIC_URI).permitAll()
                .antMatchers("/test/auth/student").authenticated()
                .anyRequest().permitAll()
//                .anyRequest().authenticated() //개발이 번거로워서 꺼놨습니다. 필요한 경우 윗줄과 주석 바꿔서 사용하시면 됩니다.
                /**세션 사용 안함*/
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                /**jwt 설정*/
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); //jwt filter 추가 (커스텀 필터, 그 후에 등록될 필터)

        return http.build();
    }


}
