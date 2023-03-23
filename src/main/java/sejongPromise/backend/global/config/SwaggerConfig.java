package sejongPromise.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import sejongPromise.backend.global.config.jwt.JwtProvider;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "세종대학교 예약해줘 API",
                version = SwaggerConfig.API_VERSION,
                description = "세종대학교 예약해줘 RESTFUL API 제공"
        )
}
)

@SecurityScheme(
        name = JwtProvider.AUTHORIZATION,
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    public static final String API_VERSION = "v1.0.0";
}
