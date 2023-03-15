package sejongPromise.backend.debug.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejongPromise.backend.debug.user.dto.request.UserRequestDto;
import sejongPromise.backend.debug.user.repository.UserInfoRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userInfoRepository;

    /**
     * @param userRequestDto
     * @return join 결과
     */
    public void join(UserRequestDto userRequestDto) {
        //Todo : 토큰 서비스를 위해 임시로 구현한 join -> 추후 user 구현되면 수정 필요

        //중복 금지
        if(userInfoRepository.findById(userRequestDto.getStudentNum()).isPresent()){
            throw new CustomException(ErrorCode.ALREADY_USER_EXIST);
        }

        UserInfo user = UserInfo.builder()
                .id(userRequestDto.getStudentNum())
                .build();

        userInfoRepository.save(user);
    }

    public void clear() {
        userInfoRepository.deleteAll();
    }

    public long count() {
        return userInfoRepository.count();
    }
}
