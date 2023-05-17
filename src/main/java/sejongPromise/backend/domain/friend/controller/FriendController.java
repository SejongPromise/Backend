package sejongPromise.backend.domain.friend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.friend.model.dto.request.RequestCreateFriendDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseFriendInfoDto;
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

    /**
     * 친구 생성 API
     *
     * @param auth Header : Authorization
     * @Param dto 요청 body (친구 학번, 이름, 닉네임)
     */
    @PostMapping("")
    @StudentAuth
    public void createFriend(CustomAuthentication auth, @Valid @RequestBody RequestCreateFriendDto dto) {
        friendService.createFriend(auth.getStudentId(), dto);
    }

    /**
     * 친구 목록 불러오기 API
     *
     * @param auth Header : Authorization
     * @return 친구 list (학번, 이름, 닉네임)
     */
    @GetMapping("")
    @StudentAuth
    public List<ResponseFriendInfoDto> getFriends(CustomAuthentication auth) {
        return friendService.getFriends(auth.getStudentId());
    }

    /**
     * 닉네임으로 친구 찾기 API
     *
     * @param auth Header : Authorization
     * @param nickname
     * @return 친구 list (학번, 이름, 닉네임)
     */
    @GetMapping("/nickname")
    @StudentAuth
    public List<ResponseFriendInfoDto> getFriendByNickname(CustomAuthentication auth,
                                                     @RequestParam("nickname") String nickname) {
        return friendService.getFriendByNickname(auth.getStudentId(), nickname);
    }

    /**
     * 친구 삭제 API
     *
     * @param auth Header : Authorization
     * @param friendId PathVariable
     */
    @DeleteMapping("/{id}")
    @StudentAuth
    public void deleteFriend(CustomAuthentication auth,
                                                     @PathVariable("id") Long friendId) {
        friendService.deleteFriend(auth.getStudentId(), friendId);
    }
}
