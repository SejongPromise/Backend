package sejongPromise.backend.domain.register.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "register 생성 request dto")
@Getter
@RequiredArgsConstructor
    public class RegisterCreateRequestDto {

    @Schema(description = "시험일자")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date; //시험일자

    @Schema(description = "시간")
    @NotNull
    @DateTimeFormat(pattern = "hh:mm:ss")
    private final LocalTime time; //시간

    @Schema(description = "책 제목")
    @NotBlank
    private final String bookTitle;

    @Schema(description = "책 영역 code")
    @NotBlank
    private final String bookAreaCode;

    @Schema(description = "책 code")
    @NotBlank
    private final String bookCode;
}
