package sejongPromise.backend.global.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {
    private final Long studentId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
