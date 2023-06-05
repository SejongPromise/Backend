package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestQuitDto {
    @NotBlank(message = "애플리케이션 비밀번호")
    private final String password;
}
