package sejongPromise.backend.global.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.user.JpaUserRepository;
import sejongPromise.backend.domain.user.UserInfo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //DB에 username을 가진 user 있는지 확인
        //학번으로 조회
        Optional<UserInfo> user = userRepository.findByStudentNum(username);

//        Optional<UserInfo> member = memberRepository.findById(Long.parseLong(username));
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("해당하는 user가 없습니다");
        }
        return new CustomUserDetails(user.get());
    }

    public UserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {        //id로 조회
        Optional<UserInfo> user = userRepository.findById(Long.parseLong(userId));
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("해당하는 user가 없습니다");
        }
        return new CustomUserDetails(user.get());
    }
}
