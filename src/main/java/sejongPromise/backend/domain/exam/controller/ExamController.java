package sejongPromise.backend.domain.exam.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamFieldInfoDto;
import sejongPromise.backend.domain.exam.model.dto.response.ResponseExamInfoDto;
import sejongPromise.backend.domain.exam.service.ExamService;
import sejongPromise.backend.global.config.jwt.JwtProvider;

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
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public List<ResponseExamInfoDto> getList(Authentication auth) {
        Long studentId = (Long) auth.getPrincipal();
        return examService.list(studentId);
    }

    /**
     * 영역별 이수 통계 조회
     * @param auth Header : Authorization
     * @return 영역별 이수 통계
     */
    @GetMapping("/field")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public List<ResponseExamFieldInfoDto> getListByField(Authentication auth){
        return examService.fieldList((Long) auth.getPrincipal());
    }

}