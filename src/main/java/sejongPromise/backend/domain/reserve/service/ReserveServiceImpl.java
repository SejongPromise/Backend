package sejongPromise.backend.domain.reserve.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sejongPromise.backend.domain.reserve.model.dto.response.ResponseReserveDto;
import sejongPromise.backend.domain.reserve.repository.ReserveRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import sejongPromise.backend.infra.sejong.service.portal.ReservingBookService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@Service
@Transactional
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReserveServiceImpl implements ReserveService{
    private final ReserveRepository reserveRepository;

    @Override
    public void reserve(Long studentId, String title) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 학생을 찾을 수 없습니다"));
        Optional<Reserve> reserve = reserveRepository.findByStudentIdAndTitle(studentId, title);

        if(reserve.isPresent()){
            throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 예약한 도서입니다");
        }
        Reserve newReserve = Reserve.builder()
                .student(student)
                .title(title)
                .status(ReserveStatus.AVAILABLE)
                .build();

        reserveRepository.save(newReserve);
    }

    @Override
    public void cancel(Long studentId, Long reserveId) {
        Optional<Reserve> reserve = reserveRepository.findByIdAndStudentId(reserveId, studentId);
        if(reserve.isEmpty()){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 예약을 찾을 수 없습니다");
        }
        reserveRepository.delete(reserve.get());
    }

    @Override
    public List<ResponseReserveDto> list(Long studentId) {
        return reserveRepository.findAllByStudentId(studentId).stream().map(ResponseReserveDto::new).collect(Collectors.toList());
    }
    @Override
    public String checkStatus(String title) {
        if(title.equals("한비자")) return ReserveStatus.AVAILABLE.toString();
        if(title.equals("한비자2")) return ReserveStatus.RESERVED.toString();
        return ReserveStatus.UNAVAILABLE.toString();
    }

}
