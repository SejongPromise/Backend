package sejongPromise.backend.domain.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.friend.model.dto.request.RequestCreateFriendDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendById;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendByNicknameDto;
import sejongPromise.backend.domain.friend.model.dto.response.ResponseGetFriendsDto;
import sejongPromise.backend.domain.friend.repository.FriendRepository;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import java.util.ArrayList;
import java.util.List;

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
        isAlreadyExistFriend(student, dto.getStudentNum());
        //nickname 중복 검사(student가 가진 친구 중에서)
        isDuplicateNickname(student, dto.getNickname());

        Friend friend = Friend.builder()
                .name(dto.getName())
                .studentNum(dto.getStudentNum())
                .nickname(dto.getNickname())
                .student(student)
                .build();

        friendRepository.save(friend);
    }

    //친구 목록 불러오기
    public List<ResponseGetFriendsDto> getFriends(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        List<Friend> friendList = friendRepository.findAllByStudent(student);
        List<ResponseGetFriendsDto> responseList = new ArrayList<>();
        for (Friend friend : friendList) {
            responseList.add(new ResponseGetFriendsDto(friend));
        }
        return responseList;
    }

    //닉네임으로 친구 찾기
    public ResponseGetFriendByNicknameDto getFriendByNickname(Long studentId, String nickname) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Friend friend = friendRepository.findByStudentAndNickname(student, nickname).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        return new ResponseGetFriendByNicknameDto(friend);
    }

    //id로 친구 찾기
    public ResponseGetFriendById getFriendById(Long studentId, Long friendId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        if (!friend.getStudent().equals(student)) {
            throw new CustomException(ErrorCode.NOT_A_FRIEND);
        }
        return new ResponseGetFriendById(friend);
    }

    //이미 존재하는 친구인지 검사
    private void isAlreadyExistFriend(Student student, Long studentNum) {
        if(friendRepository.existsByStudentAndStudentNum(student, studentNum)){
            throw new CustomException(ErrorCode.ALREADY_EXIST_FRIEND);
        }
    }

    //student 가 가진 친구 중 닉네임 중복검사
    private void isDuplicateNickname(Student student, String nickname) {
        if (friendRepository.existsByStudentAndNickname(student, nickname)) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
    }

}
