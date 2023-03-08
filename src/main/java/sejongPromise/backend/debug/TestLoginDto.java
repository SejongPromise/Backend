package sejongPromise.backend.debug;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TestLoginDto {
    private final String studentId;
    private final String password;
}
