package sejongPromise.backend.global.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomAuthentication implements Authentication {
    private final Long id;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return id;
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
