package sejongPromise.backend.domain.user;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "member")
@ToString
@Getter
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue
    private long id; //고유 id
    private String studentNum; //학번 - 로그인 id //security에서 Id는 String으로 반환돼야 함
    private String password; //비밀번호

//    private String name; //이름
//    private String department; //학과

    @Builder
    public UserInfo(String studentNum, String password) {
        this.studentNum = studentNum;
        this.password = password;
//        this.name = name;
//        this.department = department;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }

}
