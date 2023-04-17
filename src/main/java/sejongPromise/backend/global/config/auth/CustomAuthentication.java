package sejongPromise.backend.global.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import sejongPromise.backend.domain.enumerate.Role;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {
    private final Long studentId;
    private final Role role;

    public Role getRole(){
        return role;
    }
    public Long getStudentId(){
        return studentId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : role.getRole().split(",")) {
            authorities.add(() -> authority);
        }
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return studentId;
    }

    @Override
    public Object getDetails() {
        return studentId;
    }

    @Override
    public Object getPrincipal() {
        return studentId;
    }
    @Override
    public boolean isAuthenticated() {  return true; }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {    }

    @Override
    public String getName() {
        return null;
    }
}
