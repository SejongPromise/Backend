package sejongPromise.backend.domain.friend.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "friend_id")
    private Long id;

    private Long friendStudentId;
    private String name;
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "student_id")
    private Student student;

    @Builder
    public Friend(@NotNull Long friendStudentId,
                  @NotNull String name,
                  @NotNull String nickname,
                  @NotNull Student student) {
        this.friendStudentId = friendStudentId;
        this.name = name;
        this.nickname = nickname;
        this.student = student;
    }
}
