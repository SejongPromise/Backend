package sejongPromise.backend.global.config.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.config.auth.AuthenticationToken;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.error.exception.CustomException;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static sejongPromise.backend.global.error.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    public static final String AUTHORIZATION = "Authorization";
    @Value("${jwt.secret-key}")
    private final String secretKey;

    @Value("${jwt.access-expiration}")
    private final Duration accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private final Duration refreshExpiration;

    public AuthenticationToken issue(Student student){
        return AuthenticationToken.builder()
                .accessToken(createAccessToken(student.getId()))
                .refreshToken(createRefreshToken()).build();
    }

    public AuthenticationToken reissue(String accessToken, String refreshToken) {
        String validateRefreshToken = validateRefreshToken(refreshToken);
        String refreshAccessToken = refreshAccessToken(accessToken);
        return AuthenticationToken.builder()
                .accessToken(refreshAccessToken)
                .refreshToken(validateRefreshToken)
                .build();
    }

    //JWT 토큰 생성
    private String createAccessToken(Long studentId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(studentId)); //JWT payload에 저장되는 정보 단위, user 식별값 넣음 (id)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(accessExpiration);

        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant())) //토큰 발생 시간
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant())) //만료 기간
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) //암호화 알고리즘, secret 값
                .compact();
    }
    private String createRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(refreshExpiration);

        return Jwts.builder()
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant())) //토큰 발생 시간
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant())) //만료 기간
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) //암호화 알고리즘, secret 값
                .compact();
    }

    private String refreshAccessToken(String accessToken) {
        String studentId;
        try{
            Jws<Claims> claimsJws = validateAccessToken(accessToken);
            studentId = claimsJws.getBody().getSubject();
        }catch (ExpiredJwtException e){
            studentId = e.getClaims().getSubject();
        }
        return createAccessToken(Long.parseLong(studentId));
    }


    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String accessToken) {
        Jws<Claims> claimsJws = validateAccessToken(accessToken);
        Long studentId = Long.parseLong(claimsJws.getBody().getSubject());
        //발급할 때는 인증이 필요없지만 검증 시에는 user 데이터에 token의 학번과 일치하는 사용자가 있는지 확인 필요함
        return new CustomAuthentication(studentId);
    }

    //JWT 토큰에서 회원 정보(학번) 추출 -> token을 발급받을 때는 무조건 인증된 사용자만 발급받아서 회원 정보 검증할 필요가 없음

    //request header에서 token 값 가져옴
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if(!StringUtils.hasText(header)) return null;
        return request.getHeader(AUTHORIZATION);
    }

    //token 유효성, 만료일자 확인
    private Jws<Claims> validateAccessToken(String accessToken) {
        try {
            return Jwts.parser().
                    setSigningKey(secretKey.getBytes()).
                    parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRED_TOKEN);
        } catch (JwtException e){
            throw new CustomException(INVALID_TOKEN);
        }
    }

    //token 유효성, 만료일자 확인
    private String validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().
                    setSigningKey(secretKey.getBytes()).
                    parseClaimsJws(refreshToken);
            return refreshToken;
        } catch (ExpiredJwtException e) {
            throw new CustomException(EXPIRED_TOKEN);
        } catch (JwtException e){
            throw new CustomException(INVALID_TOKEN);
        }
    }

}
