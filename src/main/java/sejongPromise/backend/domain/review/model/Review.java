package sejongPromise.backend.domain.review.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.enumerate.BookRatio;
import sejongPromise.backend.domain.enumerate.ReviewStatus;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.reports.Report;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Review extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    private Integer score;

    private Integer volume;

    @Enumerated(EnumType.STRING)
    private BookRatio ratio;
    private String comment;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
    private Semester passSemester;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();


    @Builder
    private Review(@NonNull Book book,
                   @NonNull Student student,
                   @NonNull Integer score,
                   @NonNull Integer volume,
                   @NonNull BookRatio ratio,
                   String comment) {
        this.book = book;
        this.student = student;
        this.score = score;
        this.volume = volume;
        this.ratio = ratio;
        this.comment = comment;
        this.status = ReviewStatus.ACTIVE;
        this.passSemester = student.getSemester();
    }

    public void updateReview(Integer score, Integer volume, BookRatio ratio, String comment) {
        this.score = score;
        this.volume = volume;
        this.ratio = ratio;
        this.comment = comment;
        this.status = ReviewStatus.EDITED;
    }

    public void updateStatus(ReviewStatus status) {
        this.status = status;
    }

    public String displayName(){
        return this.passSemester.getName() + " " + "수강자";
    }

    public void blind() {
        this.status = ReviewStatus.DELETED_BY_ADMIN;
    }
}
