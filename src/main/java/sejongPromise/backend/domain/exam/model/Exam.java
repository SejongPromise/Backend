package sejongPromise.backend.domain.exam.model;

import lombok.*;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;
import sejongPromise.backend.infra.sejong.model.ExamInfo;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exam extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "exam_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student;
    private String year;
    @Enumerated(EnumType.STRING)
    private Semester semester;
    @Enumerated(EnumType.STRING)
    private BookField field;
    @Column(unique = true)
    private String title; // todo : 1:1 맵핑. -> 불가능 deprecated 된 시험 목록이 존재함.
    private boolean isPass;
    private boolean isTest;
    private boolean isReviewed;

    @Builder
    private Exam(@NonNull Student student,
                    @NonNull String year,
                    @NonNull String semester,
                    @NonNull String field,
                    @NonNull String title,
                    @NonNull Boolean isPass,
                    @NonNull Boolean isTest){
        this.student = student;
        this.year = year;
        this.semester = Semester.of(semester);
        this.field = BookField.of(field);
        this.title = title;
        this.isPass = isPass;
        this.isTest = isTest;
        this.isReviewed = false;
    }

    public void updateReviewed(){
        this.isReviewed = true;
    }

    public void updateExamInfo(ExamInfo newExam) {
        this.year = newExam.getYear();
        this.semester = Semester.of(newExam.getSemester().replace(" ", ""));
        this.field = BookField.of(newExam.getField());
        this.title = newExam.getTitle();
        this.isPass = newExam.isPass();
        this.isTest = newExam.isTest();
        this.isReviewed = false;
    }


}
