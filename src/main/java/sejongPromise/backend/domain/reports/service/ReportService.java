package sejongPromise.backend.domain.reports.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.ReportType;
import sejongPromise.backend.domain.reports.model.Report;
import sejongPromise.backend.domain.reports.repository.ReportRepository;
import sejongPromise.backend.domain.review.model.Review;
import sejongPromise.backend.domain.review.repository.ReviewRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;


@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final ReviewRepository reviewRepository;
    private final int REPORT_LIMIT = 10;

    /**
     * 리뷰 신고 10건의 넘어간 경우 blind 처리 합니다.
     * @param studentId 학생 id
     * @param reviewId  리뷰 id
     * @param reportIdx 신고 카테고리 id
     */
    public void report(Long studentId, Long reviewId, Integer reportIdx) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 학생을 찾을 수 없습니다."));
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 리뷰를 찾을 수 없습니다."));

        checkDuplicateReport(student.getId(), reviewId);

        Report report = Report.builder()
                .student(student)
                .review(review)
                .type(ReportType.of(reportIdx))
                .build();
        reportRepository.save(report);

        if(reportRepository.countByReviewId(reviewId) >= REPORT_LIMIT) {
            review.blind();
        }
    }

    private void checkDuplicateReport(Long studentId, Long reviewId) {
        if(reportRepository.existsByStudentIdAndReviewId(studentId, reviewId)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 신고한 리뷰입니다.");
        }
    }

}
