package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@RequiredArgsConstructor
public class RequestSignupDto {

    @NotBlank(message = "학번을 입력해주세요.")
    @Pattern(regexp = "\\d{8}$", message = "학번을 정확히 입력해주세요.")
    private final String studentId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

}
