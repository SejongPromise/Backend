package sejongPromise.backend.domain.friend.model.dto.response;

import lombok.Getter;
import sejongPromise.backend.domain.friend.model.Friend;

@Getter
public class ResponseGetFriendById {
    private final Long studentNum;
    private final String name;

    public ResponseGetFriendById(Friend friend) {
        this.studentNum = friend.getStudentNum();
        this.name = friend.getName();
    }
}
