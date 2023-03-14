package sejongPromise.backend.domain.user;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "userinfo")
@ToString
@Getter
@NoArgsConstructor
public class UserInfo {
    @Id
    private long id; //학번

    @Builder
    public UserInfo(Long id) {
        this.id = id;
    }
}
