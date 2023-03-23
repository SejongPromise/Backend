package sejongPromise.backend.debug.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity getJwtToken(Long userId){ //학번으로 jwt 토큰 발급 //요청하는 user는 이미 인증(세종포털 등)된 user로 별도의 인증 없이 발급함
        return new ResponseEntity(jwtTokenProvider.createToken(userId), HttpStatus.OK);
    }

}
