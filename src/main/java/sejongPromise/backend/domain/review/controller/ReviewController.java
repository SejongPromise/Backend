package sejongPromise.backend.domain.review.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.review.model.dto.ReviewDto;
import sejongPromise.backend.domain.review.model.dto.request.RequestCreateReviewDto;
import sejongPromise.backend.domain.review.service.ReviewService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.qualifier.StudentAuth;
import sejongPromise.backend.global.model.ResponseSlice;

import javax.validation.Valid;


@Tag(name = "REVIEW", description = "REVIEW API 모음")
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{bookId}")
    public ResponseSlice<ReviewDto> getReviewList(@PathVariable Long bookId,
                                               @PageableDefault(size = 20, sort = "create_at", direction = Sort.Direction.DESC) Pageable pageable){
        Slice<ReviewDto> list = reviewService.list(bookId, pageable);
        return new ResponseSlice<>(list);
    }

    @PostMapping("/{bookId}")
    @StudentAuth
    public Long createReview(CustomAuthentication auth,
                                @PathVariable Long bookId,
                                @RequestBody @Valid RequestCreateReviewDto dto){
        return reviewService.create(auth.getStudentId(), bookId, dto.getScore(), dto.getVolume(), dto.getRatioIdx(), dto.getComment());
    }

    @PatchMapping("/{reviewId}")
    @StudentAuth
    public Long editReview(CustomAuthentication auth,
                           @PathVariable Long reviewId,
                           @RequestBody @Valid RequestCreateReviewDto dto){
        return reviewService.edit(auth.getStudentId(), reviewId, dto.getScore(), dto.getVolume(), dto.getRatioIdx(), dto.getComment());
    }

    @DeleteMapping("/{reviewId}")
    @StudentAuth
    public void deleteReview(CustomAuthentication auth,
                             @PathVariable Long reviewId){
        reviewService.delete(auth.getStudentId(), reviewId, auth.getRole());
    }
}
