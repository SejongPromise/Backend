package sejongPromise.backend.domain.review.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;
import sejongPromise.backend.domain.review.model.dto.ReviewDto;

import java.util.List;

@Getter
public class ResponseReviewListDto {
    private List<ReviewDto> reviewList;
    private boolean isFirst;
    private boolean isLast;
    private boolean isEmpty;

    @Builder
    public ResponseReviewListDto(List<ReviewDto> reviewList, boolean isFirst, boolean isLast, boolean isEmpty) {
        this.reviewList = reviewList;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.isEmpty = isEmpty;
    }

    public static ResponseReviewListDto of(Slice<ReviewDto> reviewDtoSlice){
        return ResponseReviewListDto.builder()
                .reviewList(reviewDtoSlice.getContent())
                .isFirst(reviewDtoSlice.isFirst())
                .isLast(reviewDtoSlice.isLast())
                .isEmpty(reviewDtoSlice.isEmpty())
                .build();
    }
}
