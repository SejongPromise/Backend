package sejongPromise.backend.domain.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.friend.model.dto.request.RequestCreateFriendDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseFriendInfoDto;
import sejongPromise.backend.domain.friend.repository.FriendRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {
    private final FriendRepository friendRepository;
    private final StudentRepository studentRepository;

    //친구 생성
    public void createFriend(Long studentId, RequestCreateFriendDto dto) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        //이미 있는 친구면 생성하지 않기
        if(friendRepository.existsByStudentAndFriendStudentId(student, dto.getFriendStudentId())){
            throw new CustomException(ErrorCode.ALREADY_EXIST_FRIEND);
        }

        //todo : ipid값 제대로 얻을 수 있는지 확인, 못 얻으면 error

        Friend friend = Friend.builder()
                .name(dto.getName())
                .friendStudentId(dto.getFriendStudentId())
                .nickname(dto.getNickname())
                .student(student)
                .build();

        friendRepository.save(friend);
    }

    //친구 목록 불러오기
    public List<ResponseFriendInfoDto> getFriends(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        List<Friend> friendList = friendRepository.findAllByStudent(student);

        return friendList.stream().map(friend -> new ResponseFriendInfoDto(friend)).collect(Collectors.toList());
    }

    //닉네임으로 친구 찾기
    public List<ResponseFriendInfoDto> getFriendByNickname(Long studentId, String nickname) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        ;
        List<ResponseFriendInfoDto> friendList = friendRepository.findAllByStudentAndNickname(student, nickname)
                .stream().map(friend -> new ResponseFriendInfoDto(friend)).collect(Collectors.toList());

        return friendList;
    }

    //친구 삭제
    public void deleteFriend(Long studentId, Long friendId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        friendRepository.delete(friend);
    }

}
