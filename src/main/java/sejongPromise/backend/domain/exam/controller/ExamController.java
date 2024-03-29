package sejongPromise.backend.domain.exam.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamFieldInfoDto;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamInfoDto;
import sejongPromise.backend.domain.exam.service.ExamService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.qualifier.StudentAuth;

import java.util.List;


@Tag(name = "시험 인증 API", description = "시험 인증 API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;

    /**
     * 나의 이수 여부 조회
     *
     * @param auth Header : Authorization
     * @return 내 이수 여부
     */
    @GetMapping
    @StudentAuth
    public List<ResponseExamInfoDto> getList(CustomAuthentication auth) {
        Long studentId = auth.getStudentId();
        return examService.list(studentId);
    }

    /**
     * 영역별 이수 통계 조회
     * @param auth Header : Authorization
     * @return 영역별 이수 통계
     */
    @GetMapping("/field")
    @StudentAuth
    public List<ResponseExamFieldInfoDto> getListByField(CustomAuthentication auth){
        return examService.fieldList(auth.getStudentId());
    }

}