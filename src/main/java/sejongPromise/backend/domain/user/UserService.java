package sejongPromise.backend.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository userRepository;

    /**
     * @param userRequestDto
     * @return join 결과
     */
    public boolean join(UserRequestDto userRequestDto) {
        //토큰 서비스를 위해 임시로 구현한 join -> 추후 user 구현되면 수정 필요

        //중복 금지
        Optional<UserInfo> userById = userRepository.findById(userRequestDto.getStudentNum());
        if (userById.isPresent()) {
            return false;
        }

        UserInfo user = UserInfo.builder()
                .id(userRequestDto.studentNum)
                .build();

        userRepository.save(user);
        return true;
    }

    public Optional<UserInfo> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    public void clear() {
        userRepository.deleteAll();
    }

    public long count() {
        return userRepository.count();
    }
}
