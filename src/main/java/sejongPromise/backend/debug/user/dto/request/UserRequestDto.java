package sejongPromise.backend.debug.user.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * login, join request
 */
@Getter
@RequiredArgsConstructor
public class UserRequestDto {

    @NotNull(message = "학번은 비어 있으면 안됩니다.")
    private final Long studentNum;
}
