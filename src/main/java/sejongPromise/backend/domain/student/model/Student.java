package sejongPromise.backend.domain.student.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate // 필요한 필드만 업데이트 시킵니다.
public class Student extends BaseEntity implements Persistable<Long> {

    @Id
    @Column(name = "student_id")
    private Long id;
    private String password;
    private String sessionToken;
    private String name;
    private String major;
    @Enumerated(EnumType.STRING)
    private Semester semester;
    private Boolean isPass;

    /**
     * Persistable override
     * PK 값이 자동생성이 아닌경우 즉, 직접 PK를 설정해야 하는경우 해당 메소드를 override 해줘야함.
     * 새로운 객체인지 확인이 필요함.
     */
    @Override
    public boolean isNew() {
        return getCreate_at() == null;

    }

    @Builder
    private Student(@NonNull Long studentId,
                    @NonNull String name,
                    @NonNull String major,
                    @NonNull String encodedPassword,
                    @NonNull String sessionToken,
                    @NonNull String semester,
                    @NonNull Boolean pass){
        this.id = studentId;
        this.name = name;
        this.major = major;
        this.sessionToken = sessionToken;
        this.semester = Semester.of(semester);
        this.isPass = pass;
        this.password = encodedPassword;
    }

    public void update(Student student){
        this.major = student.getMajor();
        this.sessionToken = student.getSessionToken();
        this.semester = student.getSemester();
        this.isPass = student.isPass;
    }

    public String getStudentId() {
        return id.toString();
    }
}
