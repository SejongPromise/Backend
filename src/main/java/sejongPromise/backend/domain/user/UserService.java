package sejongPromise.backend.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sejongPromise.backend.global.config.jwt.JwtTokenProvider;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * @param userRequestDto
     * @return join 결과
     */
    public boolean join(UserRequestDto userRequestDto) {
        //크롤링해서 학과 정보, 이름 얻어야 함 //이부분 논의 필요
        //일단 입력받은 학번과 비밀번호만 저장

        //중복 금지
        Optional<UserInfo> userByStudentNum = userRepository.findByStudentNum(userRequestDto.getStudentNum());
        if (userByStudentNum.isPresent()) {
            return false;
        }

        UserInfo user = UserInfo.builder()
                .studentNum(userRequestDto.studentNum)
                .password(userRequestDto.password)
                .build();

        user.encryptPassword(passwordEncoder);
        log.info("password: {}",user.getPassword());
        userRepository.save(user);
        return true;
    }

    /**
     * @param request
     * @return jwt token
     */
    public String login(UserRequestDto request) { //login 하면 jwt 토큰 보내줘야 함
        Optional<UserInfo> user = userRepository.findByStudentNum(request.studentNum);
        if (!user.isPresent()) {
            return "UsernameNotFoundException";
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getStudentNum(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtTokenProvider.createToken(String.valueOf(user.get().getId())); //user Id만 넣음
        } catch (UsernameNotFoundException e) { //확인을 위해 exception 정보를 리턴
            return "UsernameNotFoundException";
        } catch (BadCredentialsException e) {
            return "BadCredentialsException";
        }
    }

}
