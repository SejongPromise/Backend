package sejongPromise.backend.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.student.model.Student;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByStudent(Student student);

    Optional<Friend> findByStudentAndNickname(Student student, String nickname);

    boolean existsByStudentAndNickname(Student student, String nickname);

    boolean existsByStudentAndStudentNum(Student student, Long studentNum);
}
