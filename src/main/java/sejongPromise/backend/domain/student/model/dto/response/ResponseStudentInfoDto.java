package sejongPromise.backend.domain.student.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.student.model.Student;

@Getter
public class ResponseStudentInfoDto {
    private final String userName;
    private final String studentId;
    private final String major;
    private final Integer semester;

    public ResponseStudentInfoDto(Student student){
        this.userName = student.getName();
        this.studentId = student.getStudentId();
        this.major = student.getMajor();
        this.semester = student.getSemester();
    }

}
