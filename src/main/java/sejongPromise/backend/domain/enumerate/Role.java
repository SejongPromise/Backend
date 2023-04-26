package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

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
        if(BY_LABEL.get(role) == null){
            throw new CustomException(ErrorCode.INVALID_REQUEST, "존재하지 않는 권한 입니다.");
        }
        return BY_LABEL.get(role);
    }

    public boolean isAdmin(){
        return this == ADMIN;
    }
}
