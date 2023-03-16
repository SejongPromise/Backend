package sejongPromise.backend.debug.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.debug.user.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
