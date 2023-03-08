package sejongPromise.backend.global.config.auth;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sejongPromise.backend.domain.user.UserInfo;

import java.util.ArrayList;
import java.util.Collection;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";
    private UserInfo userInfo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //user 권한 리턴
        //member의 role을 admin, manager, user 등으로 세분화할 경우 member에 role 추가, getRole 추가해줄 것
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX + "USER");
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(authority);

        return authorities;

    }

    @Override
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfo.getStudentNum();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    public Long getUserId(){ return userInfo.getId();}

}
