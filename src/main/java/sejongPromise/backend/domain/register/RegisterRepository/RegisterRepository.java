package sejongPromise.backend.domain.register.RegisterRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.student.model.Student;

import java.time.LocalDate;
import java.util.List;

public interface RegisterRepository extends JpaRepository<Register,Long> {
    List<Register> findAllByStudentId(Long studentId);

    boolean existsByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);
}
