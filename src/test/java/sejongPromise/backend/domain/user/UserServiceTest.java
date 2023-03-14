package sejongPromise.backend.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void getJwtToken() {
    }

    @Test
    void findByUserId() {
        UserRequestDto userRequestDto = new UserRequestDto(18011552L);
        userService.join(userRequestDto);

        Optional<UserInfo> byUserId = userService.findByUserId(18011552L);

        Assertions.assertThat(byUserId.get().getId()).isEqualTo(18011552L);
    }
}