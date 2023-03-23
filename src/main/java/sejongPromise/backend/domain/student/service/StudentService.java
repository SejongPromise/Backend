package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseLoginDto;
import sejongPromise.backend.domain.student.model.dto.response.ResponseStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import java.util.Currency;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public ResponseLoginDto login(RequestStudentInfoDto dto) {
        Student student = studentRepository.findById(Long.parseLong(dto.getStudentId())).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        if (passwordEncoder.matches(dto.getPassword(), student.getPassword())) {
            String token = jwtTokenProvider.createToken(Long.parseLong(dto.getStudentId()));
            return new ResponseLoginDto(student, token);
        }else{
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
    }

}
