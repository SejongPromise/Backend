package sejongPromise.backend.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.BookRatio;
import sejongPromise.backend.domain.enumerate.ReviewStatus;
import sejongPromise.backend.domain.enumerate.Role;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.domain.review.model.dto.ReviewDto;
import sejongPromise.backend.domain.review.repository.ReviewRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;

    /**
     * 리뷰 목록을 가져옵니다. 작성자는 해당 시험을 수강한 학기로 채워집니다.
     * @param bookId    책 ID
     * @param pageable  페이징 정보
     * @return          페이징 된 리뷰 목록
     */
    public Page<ReviewDto> list(Long bookId, Pageable pageable) {
        bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 찾을 수 없습니다"));
        return reviewRepository.findAllByBookId(bookId, pageable).map(ReviewDto::new);
    }

    /**
     * 리뷰 작성
     * @param studentId 학생 ID
     * @param bookId    책 ID
     * @param score     별점 ( 1 ~ 5 )
     * @param volume    몇회독
     * @param ratioIdx  도움이 된 부분
     * @param comment   리뷰 내용
     * @return
     */
    public Long create(Long studentId, Long bookId, Integer score, Integer volume, Integer ratioIdx, String comment) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 찾을 수 없습니다"));

        // todo : 리뷰 작성은 무제한 작성이 가능한가? or 제한을 둘 것인가?

        Review review = Review.builder()
                .student(student)
                .book(book)
                .score(score)
                .ratio(BookRatio.of(ratioIdx))
                .volume(volume)
                .comment(comment)
                .build();

        Review save = reviewRepository.save(review);
        return save.getId();
    }

    /**
     * 리뷰 수정
     * @param reviewId  리뷰 ID
     * @param score     별점 ( 1 ~ 5 )
     * @param volume    몇회독
     * @param ratioIdx  도움이 된 부분
     * @param comment   리뷰 내용
     */
    public Long edit(Long studentId, Long reviewId, Integer score, Integer volume, Integer ratioIdx, String comment) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 리뷰를 찾을 수 없습니다"));
        if (!review.getStudent().getId().equals(studentId)) {
            throw new CustomException(ErrorCode.NOT_GRANTED);
        }
        review.updateReview(score, volume, BookRatio.of(ratioIdx), comment);

        return review.getId();
    }

    /**
     * 리뷰 삭제
     * @param studentId 학생 ID
     * @param reviewId  리뷰 ID
     */
    public void delete(Long studentId, Long reviewId, Role role) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 리뷰를 찾을 수 없습니다"));

        if(role.isAdmin()){
            review.updateStatus(ReviewStatus.DELETED_BY_ADMIN);
        } else if (review.getStudent().getId().equals(studentId)) {
            review.updateStatus(ReviewStatus.DELETED);
        }else{
            throw new CustomException(ErrorCode.NOT_GRANTED);
        }

    }



}
