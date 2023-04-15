package sejongPromise.backend.domain.exam.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.domain.exam.model.Exam;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Getter
@RequiredArgsConstructor
public class ResponseExamInfoDto {
    private final String year;
    @Enumerated(EnumType.STRING)
    private final String semester;
    @Enumerated(EnumType.STRING)
    private final String field;
    private final String title;
    private final boolean isPass;

    public ResponseExamInfoDto(Exam exam){
        this.year = exam.getYear().toString();
        this.semester = exam.getSemester().getName();
        this.field = exam.getField().getName();   //겟네임
        this.title = exam.getTitle();
        this.isPass = exam.isPass();
    }
}