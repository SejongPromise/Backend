package sejongPromise.backend.domain.exam.model;

import lombok.*;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "exam_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private String passAt;
    private BookField field;
    private String title;
    private boolean isPass;

    @Builder
    private Exam(@NonNull Student student,
                    @NonNull String passAt,
                    @NonNull String field,
                    @NonNull String title,
                    @NonNull Boolean isPass){
        this.student = student;
        this.passAt = passAt;
        this.field = BookField.of(field);
        this.title = title;
        this.isPass = isPass;
    }
}
