package sejongPromise.backend.domain.friend.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long studentNum;
    private String name;
    private String nickname;

    @ManyToOne
    private Student student; //하나의 학생이 여러 친구 가짐, 하나의 친구는 하나의 학생 가짐

    @Builder
    public Friend(Long studentNum, String name, String nickname, Student student) {
        this.studentNum = studentNum;
        this.name = name;
        this.nickname = nickname;
        this.student = student;
    }
}
