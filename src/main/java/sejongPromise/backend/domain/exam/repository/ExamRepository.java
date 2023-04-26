package sejongPromise.backend.domain.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamFieldInfoDto;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findAllByStudentId(Long studentId);
    @Query("select new sejongPromise.backend.domain.exam.model.dto.ResponseExamFieldInfoDto(e.field, count(e.field)) " +
            "from Exam e " +
            "where e.student.id=:studentId and e.isPass=true " +
            "group by e.field " +
            "order by count(e.field) desc")
    List<ResponseExamFieldInfoDto> findCountGroupByField(Long studentId);

    List<Exam> findAllByStudentIdAndTitle(Long studentId, String title);
}
