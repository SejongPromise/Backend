package sejongPromise.backend.domain.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.exam.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}
