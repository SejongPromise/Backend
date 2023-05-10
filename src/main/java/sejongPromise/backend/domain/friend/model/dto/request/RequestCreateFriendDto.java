package sejongPromise.backend.domain.friend.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Schema(description = "친구 생성 request dto")
@Getter
@RequiredArgsConstructor
public class RequestCreateFriendDto {
    @NotNull
    private final Long studentNum;
    @NotNull
    private final String name;
    @NotNull
    private final String nickname;
}
