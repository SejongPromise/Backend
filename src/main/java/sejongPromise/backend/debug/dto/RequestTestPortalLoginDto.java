package sejongPromise.backend.debug.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestTestPortalLoginDto {
    private final String id;
    private final String password;
}
