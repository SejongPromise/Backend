package sejongPromise.backend.domain.friend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.friend.model.dto.request.RequestCreateFriendDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendById;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendByNicknameDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendsDto;
import sejongPromise.backend.domain.friend.service.FriendService;
import sejongPromise.backend.global.config.auth.CustomAuthentication;
import sejongPromise.backend.global.config.qualifier.StudentAuth;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "스터디룸 친구 API", description = "스터디룸 친구 관련 API 모음")
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    // 친구 생성
    @PostMapping("")
    @StudentAuth
    public void createFriend(CustomAuthentication auth, @Valid @RequestBody RequestCreateFriendDto dto) {
        friendService.createFriend(auth.getStudentId(), dto);
    }

    // 친구 목록 불러오기
    @GetMapping("")
    @StudentAuth
    public List<ResponseGetFriendsDto> getFriends(CustomAuthentication auth) {
        return friendService.getFriends(auth.getStudentId());
    }

    //닉네임으로 친구 찾기
    @GetMapping("/nickname")
    @StudentAuth
    public ResponseGetFriendByNicknameDto getFriendByNickname(CustomAuthentication auth,
                                                              @RequestParam("nickname") String nickname) {
        return friendService.getFriendByNickname(auth.getStudentId(), nickname);
    }

    //id로 친구 찾기
    @GetMapping("/{id}")
    @StudentAuth
    public ResponseGetFriendById getFriendByNickname(CustomAuthentication auth,
                                                     @PathVariable("id") Long id) {
        return friendService.getFriendById(auth.getStudentId(), id);
    }

    //친구 삭제
}
