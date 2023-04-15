package sejongPromise.backend.domain.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.exam.model.Exam;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findAllByStudentId(Long studentId);
    List<Exam> findAllByIsPassAndField( Boolean isPass, BookField field);
}
