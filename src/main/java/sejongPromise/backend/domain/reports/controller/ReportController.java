package sejongPromise.backend.domain.reports.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.domain.reports.service.ReportService;

@Tag(name = "REPORT", description = "REPORT API 모음")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
    private final ReportService reportService;


}
