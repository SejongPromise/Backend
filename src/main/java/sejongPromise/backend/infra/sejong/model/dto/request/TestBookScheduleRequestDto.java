package sejongPromise.backend.infra.sejong.model.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class TestBookScheduleRequestDto {
    @NotNull
    private String shInfoId;
    private String opTermId;
    @NotNull
    private String bkAreaCode;
    @NotNull
    private String bkCode;

    @Builder
    public TestBookScheduleRequestDto(String shInfoId, String bkAreaCode, String bkCode) {
        this.shInfoId = shInfoId;
        this.opTermId = "TERM-00566";
        this.bkAreaCode = bkAreaCode;
        this.bkCode = bkCode;
    }
}
