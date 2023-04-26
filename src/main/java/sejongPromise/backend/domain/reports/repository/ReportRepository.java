package sejongPromise.backend.domain.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.reports.Report;

public interface ReportRepository extends JpaRepository<Report, Long>{

    Long countByReviewId(Long reviewId);
    Boolean existsByStudentIdAndReviewId(Long studentId, Long reviewId);

}
