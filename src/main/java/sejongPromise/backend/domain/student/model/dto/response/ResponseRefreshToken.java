package sejongPromise.backend.domain.student.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.global.config.auth.AuthenticationToken;

@Getter
public class ResponseRefreshToken {
    private final String accessToken;
    private final String refreshToken;

    public ResponseRefreshToken(AuthenticationToken token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
