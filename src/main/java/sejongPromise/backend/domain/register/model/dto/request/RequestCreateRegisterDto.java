package sejongPromise.backend.domain.register.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import sejongPromise.backend.domain.enumerate.Semester;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "register 생성 request dto")
@Getter
@RequiredArgsConstructor
    public class RequestCreateRegisterDto {

    @Schema(description = "시험일자")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date; //시험일자

    @Schema(description = "시간")
    @NotNull
    @DateTimeFormat(pattern = "hh:mm:ss")
    private final LocalTime time; //시간

    @Schema(description = "년도")
    @NotNull
    private final Integer year; // 년도

    @Schema(description = "학기")
    @NotNull
    private final String semester; //학기

    @Schema(description = "책 제목")
    @NotBlank
    private final String bookTitle;

    @Schema(description = "예약 신청 버튼값")
    @NotBlank
    private final String shInfoId;
}
