package sejongPromise.backend.domain.reports.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.reports.model.dto.request.RequestCreateReportDto;
import sejongPromise.backend.domain.reports.service.ReportService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.qualifier.AdminAuth;
import sejongPromise.backend.global.config.qualifier.StudentAuth;


@Tag(name = "REPORT", description = "REPORT API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;

    /**
     * 리뷰 신고
     * @param reviewId 리뷰 id
     * @param dto      신고 카테고리 id
     */
    @PostMapping("/{reviewId}")
    @StudentAuth
    public void report(CustomAuthentication auth, @PathVariable Long reviewId, RequestCreateReportDto dto) {
        Long studentId = auth.getStudentId();
        reportService.report(studentId, reviewId, dto.getReportIdx());
    }

}
