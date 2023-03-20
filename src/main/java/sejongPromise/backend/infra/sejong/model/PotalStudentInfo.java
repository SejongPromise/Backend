package sejongPromise.backend.infra.sejong.model;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PotalStudentInfo {
    private String studentName;
    private String studentId;
    private String major;
}
