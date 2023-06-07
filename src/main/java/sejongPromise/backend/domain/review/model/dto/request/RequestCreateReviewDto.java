package sejongPromise.backend.domain.review.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;

@Getter
@RequiredArgsConstructor
public class RequestCreateReviewDto {
    @Max(value = 5, message = "점수는 5점 이하로 입력해주세요.")
    private final Float score;
    @Max(value = 5, message = "회독은 5회 이하로 입력해주세요.")
    private final Integer volume;
    @Max(value = 2, message = "0 - 초반, 1 - 중반, 2 - 후반")
    private final Integer ratioIdx;
    private final String comment;
}
