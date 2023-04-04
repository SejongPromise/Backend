package sejongPromise.backend.domain.register.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Schema(description = "register 생성 request dto")
@Getter
@RequiredArgsConstructor
public class RegisterTestApplyRequestDto {
    @Schema(description = "책 영역 code")
    @NotBlank
    private final String bookAreaCode;

    @Schema(description = "책 code")
    @NotBlank
    private final String bookCode;

    @Schema(description = "예약 신청 버튼값")
    @NotBlank
    private final String shInfoId;
}
