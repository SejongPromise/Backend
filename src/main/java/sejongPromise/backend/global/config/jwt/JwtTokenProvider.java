package sejongPromise.backend.global.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sejongPromise.backend.debug.user.UserInfo;
import sejongPromise.backend.debug.user.repository.UserInfoRepository;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String AUTHORIZATION = "Authorization";
    @Value("${jwt.password}")
    private final String secretKey;

    @Value("${jwt.valid-time}")
    private final long tokenValidTime;

    private final UserInfoRepository userService;

    //JWT 토큰 생성
    public String createToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId)); //JWT payload에 저장되는 정보 단위, user 식별값 넣음 (id)
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발생 시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) //만료 기간
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) //암호화 알고리즘, secret 값
                .compact();
    }

    //JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        //발급할 때는 인증이 필요없지만 검증 시에는 user 데이터에 token의 학번과 일치하는 사용자가 있는지 확인 필요함
        Long userId = this.getUserId(token);
        log.info("userId: {}",this.getUserId(token));
        Optional<UserInfo> byUserId = userService.findById(this.getUserId(token));
        CustomAuthentication authentication = new CustomAuthentication(userId);

        //null 체크
        byUserId.orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_DATA,"사용자 존재하지 않음")); //되는건지잘;
        return authentication;
    }

    //JWT 토큰에서 회원 정보(학번) 추출 -> token을 발급받을 때는 무조건 인증된 사용자만 발급받아서 회원 정보 검증할 필요가 없음
    public Long getUserId(String token) {
        return Long.parseLong(Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token).getBody().getSubject());
    }

    //request header에서 token 값 가져옴
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    //token 유효성, 만료일자 확인
    public boolean isValidateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
