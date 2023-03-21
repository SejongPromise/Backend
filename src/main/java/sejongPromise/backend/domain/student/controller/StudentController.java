package sejongPromise.backend.domain.student.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.domain.student.model.dto.request.RequestSignupDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.service.StudentService;
import javax.validation.Valid;

@Tag(name = "대양휴머니티 칼리지 API", description = "대양 휴머니티 칼리지 API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * 대양휴머니티 칼리지 로그인 및 회원가입 API
     * @param dto 요청 body (학번 : 8자리 , 비번 : 자유형식)
     * @return 학번, 학과, 이름, 학기
     */
    @PostMapping("/signup")
    public ResponseStudentInfoDto save(@RequestBody @Valid RequestSignupDto dto){
        return studentService.save(dto);
    }

}
