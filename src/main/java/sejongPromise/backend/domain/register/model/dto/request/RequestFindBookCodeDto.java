package sejongPromise.backend.domain.register.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotNull;


@Schema(description = "책 제목과 영역코드로 코드 찾기 dto")
@Getter
@RequiredArgsConstructor
public class RequestFindBookCodeDto {
    @Schema(description = "책 영역 코드")
    @NotNull
    private final String bookAreaCode;

    @Schema(description = "책 이름")
    @NotNull
    private final String bookTitle;
}
