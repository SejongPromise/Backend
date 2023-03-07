package sejongPromise.backend.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public void join(UserRequestDto userRequestDto) {
        //크롤링해서 학과 정보, 이름 얻어야 함 //이부분 논의 필요
        //일단 입력받은 학번과 비밀번호만 저장

        UserInfo user = UserInfo.builder()
                .studentNum(userRequestDto.studentNum)
                .password(userRequestDto.password)
                .build();

        userRepository.save(user);
    }

    public String login(UserRequestDto userRequestDto) { //login 하면 jwt 토큰 보내줘야 함
        Optional<UserInfo> user = userRepository.findByStudentNum(userRequestDto.studentNum);
        if (!user.isPresent()) {
            return "";
        }
        return jwtTokenProvider.createToken(userRequestDto.getStudentNum());
    }

}
