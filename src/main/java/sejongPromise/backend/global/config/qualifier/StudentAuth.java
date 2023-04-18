package sejongPromise.backend.global.config.qualifier;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.annotation.Secured;
import sejongPromise.backend.global.config.auth.AuthNames;
import sejongPromise.backend.global.config.jwt.JwtProvider;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = JwtProvider.AUTHORIZATION)
@Secured(AuthNames.ROLE_STUDENT)
public @interface StudentAuth {
}
