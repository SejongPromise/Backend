package sejongPromise.backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * login, join request
 */
@Getter
@NoArgsConstructor
public class UserRequestDto {
    @NotNull
    Long studentNum;

    public UserRequestDto(Long studentNum) {
        this.studentNum = studentNum;
    }
}
