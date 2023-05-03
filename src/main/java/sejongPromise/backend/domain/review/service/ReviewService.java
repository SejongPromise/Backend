package sejongPromise.backend.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.BookRatio;
import sejongPromise.backend.domain.enumerate.ReviewStatus;
import sejongPromise.backend.domain.enumerate.Role;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.repository.ExamRepository;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.domain.review.model.dto.ReviewDto;
import sejongPromise.backend.domain.review.model.dto.response.ResponseReviewListDto;
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
    private final ExamRepository examRepository;

    /**
     * 리뷰 목록을 가져옵니다. 작성자는 해당 시험을 수강한 학기로 채워집니다.
     * @param bookId    책 ID
     * @param pageable  페이징 정보
     * @return          페이징 된 리뷰 목록
     */
    public ResponseReviewListDto list(Long bookId, Pageable pageable) {
        bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 찾을 수 없습니다"));
        Slice<ReviewDto> reviewDtoSlice = reviewRepository.findAllByBookId(bookId, pageable).map(ReviewDto::new);

        return ResponseReviewListDto.of(reviewDtoSlice);
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

        validateReview(student, book);

        Review review = Review.builder()
                .student(student)
                .book(book)
                .score(score)
                .ratio(BookRatio.of(ratioIdx))
                .volume(volume)
                .comment(comment)
                .build();

        Review save = reviewRepository.save(review);

        checkReviewed(student, book);

        return save.getId();
    }


    /**
     * 해당 검증 로직은 학생 정보를 update 할 경우, 기존 미인증 시험 -> 인증 시험으로 바뀐다는 전제하에 적용됩니다.
     */
    private void validateReview(Student student, Book book) {
        Exam exam = examRepository.findByStudentIdAndTitle(student.getId(), book.getTitle()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 수강한 시험이 없습니다"));
        if(!exam.isTest()){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 수강한 시험이 없습니다");
        }
        if(exam.isReviewed()){
            throw new CustomException(ErrorCode.ALREADY_REVIEWED);
        }
    }

    /**
     * 해당 시험을 인증하고, 리뷰를 작성했다면 리뷰 상태를 수정합니다.
     */
    private void checkReviewed(Student student, Book book) {
        Exam exam = examRepository.findByStudentIdAndTitle(student.getId(), book.getTitle()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 책을 수강한 시험이 없습니다"));
        exam.updateReviewed();
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
