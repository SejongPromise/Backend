package sejongPromise.backend.debug.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * login, join request
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotNull(message = "학번은 비어 있으면 안됩니다.")
    private Long studentNum;
}
