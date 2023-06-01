package sejongPromise.backend.domain.book.model;

import lombok.*;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.enumerate.BookStatus;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.global.model.BaseEntity;
import javax.persistence.*;

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
    @OneToOne(mappedBy = "book", fetch = FetchType.LAZY)
    private Review review;
    public float getScore(){
        if(review != null){
            return review.getScore();
        } else {
            return 0.0f;}
    }
    @Builder
    private Book(@NonNull String title,
                 @NonNull BookField field,
                 @NonNull String writer,
                 @NonNull String com,
                 @NonNull String imageUrl,
                 Review review) {
        this.title = title;
        this.field = field;
        this.writer = writer;
        this.com = com;
        this.imageUrl = imageUrl;
        this.status = BookStatus.ACTIVE;
        this.review = review;
    }

    public void deprecated() {
        this.status = BookStatus.DEPRECATED;
    }

    public void updateCode(Long code) {
        this.code = code;
    }
}
