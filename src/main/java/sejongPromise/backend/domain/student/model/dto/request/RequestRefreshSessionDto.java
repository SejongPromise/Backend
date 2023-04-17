package sejongPromise.backend.domain.student.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestRefreshSessionDto {
    @NotBlank(message = "대양휴머니티 컬리지 비밀번호")
    private final String password;
}
