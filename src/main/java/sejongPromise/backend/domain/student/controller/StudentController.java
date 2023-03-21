package sejongPromise.backend.domain.student.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.domain.student.model.dto.request.RequestSingUpDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.service.StudentService;

import javax.validation.Valid;

/**
 * @ResponseBody : 반환되는 Object 를 Json 형식으로 바꿔주는 것.
 * @Controller : 이 클래스는 API 를 생성하는 클래스 입니다.
 */
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
    public ResponseStudentInfoDto save(@RequestBody @Valid RequestSingUpDto dto){
        return studentService.save(dto);
    }

}
