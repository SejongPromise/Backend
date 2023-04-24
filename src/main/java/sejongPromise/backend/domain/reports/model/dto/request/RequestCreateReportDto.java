package sejongPromise.backend.domain.reports.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;

@Getter
@RequiredArgsConstructor
public class RequestCreateReportDto {
    @Max(value = 2, message = "0 - 신성모독, 1 - 비방, 2 - 기타")
    private final Integer reportIdx;
}
