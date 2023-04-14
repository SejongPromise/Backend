package sejongPromise.backend.domain.register.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotNull;

@Schema(description = "시험 예약 취소 request dto")
@Getter
@RequiredArgsConstructor
public class RequestCancelRegisterDto {
    @Schema(description = "register id")
    @NotNull
    private final Long registerId;

}
