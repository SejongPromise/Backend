package sejongPromise.backend.domain.review.model.dto.Response;

import lombok.Getter;
import sejongPromise.backend.domain.enumerate.BookRatio;
import sejongPromise.backend.domain.review.model.Review;


@Getter
public class ResponseReviewDto {
    private final Float score;
    private final Integer volume;
    private final BookRatio ratio;

    public ResponseReviewDto(Review review) {
        this.score = review.getScore();
        this.volume = review.getVolume();
        this.ratio = review.getRatio();
    }

}
