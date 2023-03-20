package sejongPromise.backend.domain.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.student.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
