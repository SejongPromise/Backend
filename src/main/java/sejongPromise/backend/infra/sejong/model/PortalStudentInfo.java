package sejongPromise.backend.infra.sejong.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PortalStudentInfo {
    private String studentName;
    private String studentId;
    private String major;
}
