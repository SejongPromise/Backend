package sejongPromise.backend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream

import java.util.Optional;

=======
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
>>>>>>> Stashed changes
public interface JpaUserRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByStudentNum(String studentNum);
}
