package sejongPromise.backend.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sejongPromise.backend.debug.user.UserService;
import sejongPromise.backend.debug.user.dto.request.UserRequestDto;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @AfterEach
    public void afterEach(){
        userService.clear();
    }

    @Test
    void join() {
        UserRequestDto userRequestDto = new UserRequestDto(18011552L);
        userService.join(userRequestDto);

        Assertions.assertThat(userService.count()).isEqualTo(1L);
    }


}