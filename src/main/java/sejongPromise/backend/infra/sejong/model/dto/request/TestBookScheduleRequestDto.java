package sejongPromise.backend.infra.sejong.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestBookScheduleRequestDto {
    private String shInfoId;
    private String opTermId;
    private String bkAreaCode;
    private String bkCode;

}
