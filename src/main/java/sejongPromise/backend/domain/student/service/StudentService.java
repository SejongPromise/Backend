package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sejongPromise.backend.domain.enumerate.StudentStatus;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestRefreshTokenDto;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseLoginDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseRefreshToken;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.config.auth.AuthenticationToken;
import sejongPromise.backend.global.config.jwt.JwtProvider;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;


    public ResponseLoginDto login(RequestStudentInfoDto dto) {
        Student student = studentRepository.findById(Long.parseLong(dto.getStudentId())).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        // 계정 정보 확인
        if(student.getStudentStatus() == StudentStatus.Deleted){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA);
        }
        if(student.getStudentStatus() == StudentStatus.DeletedByAdmin){
            throw new CustomException(ErrorCode.INVALID_ACCESS, "관리자에 의해 삭제된 계정입니다.");
        }

        //비밀번호 확인
        if (passwordEncoder.matches(dto.getPassword(), student.getPassword())) {
            AuthenticationToken token = jwtProvider.issue(student);
            return new ResponseLoginDto(student, token);
        }else{
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
    }


    public ResponseStudentInfoDto getStudentInfo(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        return new ResponseStudentInfoDto(student);
    }

    public ResponseRefreshToken refreshToken(HttpServletRequest request, @Valid RequestRefreshTokenDto dto) {
        String accessToken = jwtProvider.resolveToken(request);
        AuthenticationToken token = jwtProvider.reissue(accessToken, dto.getRefreshToken());
        return new ResponseRefreshToken(token);
    }

}
