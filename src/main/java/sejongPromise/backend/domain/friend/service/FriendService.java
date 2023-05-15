package sejongPromise.backend.domain.friend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.friend.model.Friend;
import sejongPromise.backend.domain.friend.model.dto.request.RequestFriendInfoDto;
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
    public void createFriend(Long studentId, RequestFriendInfoDto dto) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        //todo: 이미 있는 친구, 이미 있는 닉네임 -> and ( or ) 연산을 해야해서 @Query 필요
        // -> exist를 select 단에 못쓰고 limit 못씀, 하려면 querydsl 필요 (커스텀 레포 만들기) -> 각각 따로 만들어서 쿼리 두번 날리기 vs 이거 하나를 위해서 커스텀 레포 만들기

        //이미 있는 친구면 생성하지 않기
        isAlreadyExistFriend(student, dto.getFriendStudentId());
        //nickname 중복 검사(student가 가진 친구 중에서)
        isDuplicateNickname(student, dto.getNickname());

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
    public ResponseFriendInfoDto getFriendByNickname(Long studentId, String nickname) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Friend friend = friendRepository.findByStudentAndNickname(student, nickname).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        return new ResponseFriendInfoDto(friend);
    }

    //친구 삭제
    public void deleteFriend(Long studentId, Long friendId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
        Friend friend = friendRepository.findById(friendId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));

        friendRepository.delete(friend);
    }

    //이미 존재하는 친구인지 검사
    private void isAlreadyExistFriend(Student student, Long friendStudentId) {
        if(friendRepository.existsByStudentAndFriendStudentId(student, friendStudentId)){
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
