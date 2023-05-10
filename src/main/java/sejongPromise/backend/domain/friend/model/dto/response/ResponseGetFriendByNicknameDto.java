package sejongPromise.backend.domain.friend.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.friend.model.Friend;

@Getter
public class ResponseGetFriendByNicknameDto {
    private Long id;
    private Long studentNum;
    private String name;

    public ResponseGetFriendByNicknameDto(Friend friend) {
        this.id = friend.getId();
        this.studentNum = friend.getStudentNum();
        this.name = friend.getName();
    }
}
