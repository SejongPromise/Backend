package sejongPromise.backend.domain.student.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.student.model.dto.request.RequestRefreshTokenDto;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseLoginDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseRefreshToken;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.service.SignupService;
import sejongPromise.backend.domain.student.service.StudentService;
import sejongPromise.backend.global.config.jwt.JwtProvider;

import javax.servlet.http.HttpServletRequest;
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
    private final SignupService signupService;

    /**
     * 대양휴머니티 칼리지 회원가입 API
     *
     * @param dto 요청 body (학번 : 8자리 , 비번 : 자유형식)
     * @return 학번, 학과, 이름, 학기
     */
    @PostMapping("/signup")
    public void signup(@RequestBody @Valid RequestStudentInfoDto dto) {
        signupService.signup(dto);
    }

    /**
     * 로그인 API
     *
     * @param dto 요청 body (학번 : 8자리, 비번 : 자유형식)
     * @return 토큰, 학번, 학과, 이름, 학기
     */
    @PostMapping("/login")
    public ResponseLoginDto login(@RequestBody @Valid RequestStudentInfoDto dto) {
        return studentService.login(dto);
    }

    /**
     * 내정보 조회
     *
     * @param auth Header : Authorization
     * @return 내 정보
     */
    @GetMapping
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public ResponseStudentInfoDto getMyInfo(Authentication auth) {
        Long studentId = (Long) auth.getPrincipal();
        return studentService.getStudentInfo(studentId);
    }

    /**
     * 토큰 재발급
     * @param request Header : Authorization
     * @param dto refreshToken
     * @return 토큰 재발급
     */
    @PostMapping("/reissue")
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public ResponseRefreshToken refreshToken(HttpServletRequest request,
                                             @RequestBody @Valid RequestRefreshTokenDto dto){
        return studentService.refreshToken(request, dto);
    }
}
