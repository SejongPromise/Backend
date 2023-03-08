package sejongPromise.backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

<<<<<<< Updated upstream
=======
/**
 * login, join request
 */
>>>>>>> Stashed changes
@AllArgsConstructor
@Getter
public class UserRequestDto {
    @NotNull
    String studentNum;
    @NotNull
    String password;
}
