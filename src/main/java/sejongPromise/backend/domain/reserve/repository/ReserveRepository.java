package sejongPromise.backend.domain.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.reserve.model.Reserve;
import sejongPromise.backend.domain.student.model.Student;

import java.util.List;
import java.util.Optional;



public interface ReserveRepository extends JpaRepository<Reserve, Long>{
}
