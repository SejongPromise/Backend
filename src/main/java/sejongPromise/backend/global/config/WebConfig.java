package sejongPromise.backend.global.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.netty.http.client.HttpClient;
import sejongPromise.backend.global.interceptor.VoidToSuccessResponseInterceptor;

import javax.net.ssl.SSLException;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final VoidToSuccessResponseInterceptor voidToSuccessResponseInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(voidToSuccessResponseInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    @Primary
    public WebClient plainWebClient(){
        return WebClient.create();
    }

    @Bean
    @Qualifier("chromeAgentWebClient")
    public WebClient chromeAgentWebClient() throws SSLException{

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t ->
                        t.sslContext(sslContext));


        return WebClient.builder()
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
