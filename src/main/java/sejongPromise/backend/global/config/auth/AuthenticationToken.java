package sejongPromise.backend.global.config.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthenticationToken {
    private String accessToken;
    private String refreshToken;
}
