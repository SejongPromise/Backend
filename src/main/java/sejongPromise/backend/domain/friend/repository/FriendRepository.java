package sejongPromise.backend.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.student.model.Student;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByStudent(Student student);

    boolean existsByStudentAndFriendStudentId(Student student, Long friendStudentId);

    List<Friend> findAllByStudentAndNickname(Student student, String nickname);

}
