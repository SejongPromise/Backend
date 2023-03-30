package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestRefreshTokenDto {
    @NotBlank(message = "refreshToken is required")
    private final String refreshToken;
}
