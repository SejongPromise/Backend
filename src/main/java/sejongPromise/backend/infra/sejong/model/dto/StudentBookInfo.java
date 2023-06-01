package sejongPromise.backend.infra.sejong.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudentBookInfo {
    private final String title;
    private final Long appCount;
}
