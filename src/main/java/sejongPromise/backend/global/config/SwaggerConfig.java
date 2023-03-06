package sejongPromise.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "세종대학교 예약해줘 API",
                version = SwaggerConfig.API_VERSION,
                description = "세종대학교 예약해줘 RESTFUL API 제공"
        )
)
public class SwaggerConfig {
    public static final String API_VERSION = "v1.0.0";
}