package sejongPromise.backend.domain.book.model;

import lombok.*;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.enumerate.BookStatus;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.global.model.BaseEntity;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "book_id")
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private BookField field;
    private String writer;
    private String com;
    private String imageUrl;
    private Long code;
    @Enumerated(EnumType.STRING)
    private BookStatus status;
    private Float averageScore;

    @Builder
    private Book(@NonNull String title,
                 @NonNull BookField field,
                 @NonNull String writer,
                 @NonNull String com,
                 @NonNull String imageUrl) {
        this.title = title;
        this.field = field;
        this.writer = writer;
        this.com = com;
        this.imageUrl = imageUrl;
        this.status = BookStatus.ACTIVE;
    }

    public void deprecated() {
        this.status = BookStatus.DEPRECATED;
    }

    public void updateCode(Long code) {
        this.code = code;
    }

    public void setAverageScore(float averageScore){
        this.averageScore = averageScore;
    }
}
