package sejongPromise.backend.domain.friend.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.friend.model.Friend;

@Getter
public class ResponseGetFriendsDto {
    private final Long id;
    private final Long studentNum;
    private final String name;
    private final String nickname;

    public ResponseGetFriendsDto(Friend friend) {
        this.id = friend.getId();
        this.studentNum = friend.getStudentNum();
        this.name = friend.getName();
        this.nickname = friend.getNickname();
    }
}
