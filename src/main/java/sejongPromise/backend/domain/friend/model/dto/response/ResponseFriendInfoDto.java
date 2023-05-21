package sejongPromise.backend.domain.friend.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.friend.model.Friend;

@Getter
public class ResponseFriendInfoDto {
    private final Long friendStudentId;
    private final String name;
    private final String nickname;


    public ResponseFriendInfoDto(Friend friend) {
        this.friendStudentId = friend.getFriendStudentId();
        this.name = friend.getName();
        this.nickname = friend.getNickname();
    }
}
