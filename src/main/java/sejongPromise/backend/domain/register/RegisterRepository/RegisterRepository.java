package sejongPromise.backend.domain.register.RegisterRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.register.model.Register;

import java.util.List;

public interface RegisterRepository extends JpaRepository<Register,Long> {
    List<Register> findAllByStudentId(Long studentId);
}
