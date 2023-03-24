package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestRefreshSessionDto {
    @NotBlank(message = "비밀번호는 비어있으면 안됩니다.")
    private final String password;
}
