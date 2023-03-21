package sejongPromise.backend.domain.student.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseStudentInfoDto {
    private final String studentId;
    private final String major;
    private final String name;
    private final Integer semester;
}
