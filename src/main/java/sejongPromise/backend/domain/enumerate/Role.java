package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sejongPromise.backend.global.config.auth.AuthNames.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    STUDENT(ROLE_STUDENT),
    ADMIN(combine(ROLE_ADMIN, ROLE_STUDENT));

    private final String role;

    private static final Map<String, Role> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(Role::getRole, e -> e));

    public static Role of(String role) {
        return BY_LABEL.get(role);
    }

    public boolean isAdmin(){
        return this == ADMIN;
    }
}
