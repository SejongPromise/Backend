package sejongPromise.backend.domain.reports.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejongPromise.backend.domain.enumerate.ReportType;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Builder
    private Report(Student student, Review review, ReportType type) {
        this.student = student;
        this.review = review;
        this.type = type;
    }

}
