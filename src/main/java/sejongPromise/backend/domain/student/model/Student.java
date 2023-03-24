package sejongPromise.backend.domain.student.model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;
import sejongPromise.backend.global.model.BaseEntity;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate // 필요한 필드만 업데이트 시킵니다.
public class Student extends BaseEntity implements Persistable<Long> {

    @Id
    @Column(name = "student_id")
    private Long id;
    private String password;
    private String ssoToken;
    private String sessionToken;
    private String name;
    private String major;
    private boolean pass;
    private Integer semester;

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
                    String ssoToken, String sessionToken, Integer semester, boolean pass){
        this.id = studentId;
        this.name = name;
        this.major = major;
        this.ssoToken = ssoToken;
        this.sessionToken = sessionToken;
        this.semester = semester;
        this.pass = pass;
        this.password = encodedPassword;
    }

    public void updateSession(SejongAuth auth, ClassicStudentInfo studentInfo){
        this.major = studentInfo.getMajor();
        this.sessionToken = WebUtil.makeCookieString(auth.cookies);
        this.semester = Integer.parseInt(studentInfo.getSemester().substring(0, 1));
        this.pass = studentInfo.isPass();
    }

    public String getStudentId() {
        return id.toString();
    }
}
