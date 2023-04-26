package sejongPromise.backend.domain.review.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sejongPromise.backend.domain.review.model.Review;

import java.time.format.DateTimeFormatter;

@Getter
public class ReviewDto {
    @Schema(description = "리뷰 아이디", example = "1")
    private final Long id;

    @Schema(description = "작성자", example = "6학기")
    private final String writer;
    @Schema(description = "점수", example = "5")
    private final Integer score;

    @Schema(description = "몇회독", example = "1")
    private final Integer volume;

    @Schema(description = "도움이 된 부분", example = "FIRST")
    private final String ratio;

    @Schema(description = "리뷰 내용", example = "책이 너무 좋아요")
    private final String comment;
    @Schema(description = "생성날짜")
    private final String createdAt;

    public ReviewDto(Review review) {
        this.id = review.getId();
        this.writer = review.displayName();
        this.createdAt = review.getCreate_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.score = review.getScore();
        this.volume = review.getVolume();
        this.ratio = review.getRatio().toString();
        this.comment = review.getComment();
    }
}
