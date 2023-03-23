package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExamInfo {
    private final String passAt;
    private final String field;
    private final String title;
    private final boolean isPass;
}
