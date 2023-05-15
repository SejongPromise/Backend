package sejongPromise.backend.domain.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.student.model.Student;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByStudent(Student student);

    Optional<Friend> findByStudentAndNickname(Student student, String nickname);

    boolean existsByStudentAndNickname(Student student, String nickname);

    boolean existsByStudentAndFriendStudentId(Student student, Long friendStudentId);

//    @Query("select f from Friend f where f.student=:student and (f.friendStudentId = :friendStudentId or f.nickname = :nickname) limit 1")
//    boolean existsByStudentAndNicknameOrFriendStudentId(Student student,String nickname, Long friendStudentId);
}
