package sejongPromise.backend.domain.reports.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.ReportField;
import sejongPromise.backend.domain.reports.repository.ReportRepository;
import sejongPromise.backend.domain.review.repository.ReviewRepository;
import sejongPromise.backend.domain.student.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final ReviewRepository reviewRepository;
    private final int REPORT_LIMIT = 10;

    public void report(Long studentId, Long reviewId, String reportText) {

    }

    public List<ReportField> getReportList(){

        return null;
    }


}
