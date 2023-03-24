package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestRefreshTokenDto {
    @NotBlank(message = "refreshToken 필수")
    private final String refreshToken;
}
