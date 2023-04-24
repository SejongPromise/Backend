package sejongPromise.backend.domain.exam.model;

import lombok.*;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;

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
    @Enumerated(EnumType.STRING)
    private BookField field;
    private String title; // todo : 1:1 맵핑.
    private boolean isPass;
    private boolean isTest;
    private boolean isReviewed;
    private LocalDate examDate;

    @Builder
    private Exam(@NonNull Student student,
                    @NonNull String field,
                    @NonNull String title,
                    @NonNull Boolean isPass,
                    @NonNull Boolean isTest,
                    @NonNull LocalDate examDate) {
        this.student = student;
        this.field = BookField.of(field);
        this.title = title;
        this.isPass = isPass;
        this.isTest = isTest;
        this.examDate = examDate;
        this.isReviewed = false;
    }

}
