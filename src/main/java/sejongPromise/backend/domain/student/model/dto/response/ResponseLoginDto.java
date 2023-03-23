package sejongPromise.backend.domain.student.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.config.auth.AuthenticationToken;

@Getter
public class ResponseLoginDto {
    private final String accessToken;
    private final String refreshToken;
    private final String userName;
    private final String studentId;
    private final String major;
    private final Integer semester;

    public ResponseLoginDto(Student student, AuthenticationToken token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.userName = student.getName();
        this.studentId = student.getStudentId();
        this.major = student.getMajor();
        this.semester = student.getSemester();
    }
}
