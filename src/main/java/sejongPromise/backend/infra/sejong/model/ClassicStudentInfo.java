package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClassicStudentInfo {
    private final String major;
    private final String studentId;
    private final String name;
    private final String semester;
    private final boolean isPass;
}
