package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestQuitDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;





@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuitService {

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void quit(Long studentId, RequestQuitDto dto) {

        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        if(!passwordEncoder.matches(dto.getPassword(), student.getPassword())){
            throw new CustomException(ErrorCode.WRONG_PASSWORD, "비밀번호가 일치하지 않습니다.");
        }

        student.quit();
    }

    @Transactional
    public void quitByAdmin(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        student.quitByAdmin();
    }
}
