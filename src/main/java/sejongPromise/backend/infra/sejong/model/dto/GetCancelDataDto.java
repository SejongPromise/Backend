package sejongPromise.backend.infra.sejong.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class GetCancelDataDto {
    private final LocalDate date;
}
