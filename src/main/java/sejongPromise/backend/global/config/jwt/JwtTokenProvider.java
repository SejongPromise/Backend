package sejongPromise.backend.global.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sejongPromise.backend.global.config.auth.CustomUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey = "sejongPromiseSecret";
    private long tokenValidTime = 30 * 60 * 1000L; //토큰 유효시간 30분

    private final CustomUserDetailsService userDetailsService;
    
    protected void init() { //객체 초기화, secretKey 인코딩
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT 토큰 생성
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId); //JWT payload에 저장되는 정보 단위, user 식별값 넣음 (id)
//        claims.put("roles", roles);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발생 시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) //만료 기간
                .signWith(SignatureAlgorithm.HS256, secretKey) //암호화 알고리즘, secret 값
                .compact();
    }

    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUserId(this.getUserId(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    //JWT 토큰에서 회원 정보 추출
    public String getUserId(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //request header에서 token 값 가져옴
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    //token 유효성, 만료일자 확인
    public boolean isValidateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
