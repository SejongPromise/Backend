package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

@RequiredArgsConstructor
public class SejongScrapper {
    private final WebClient webClient;

    protected String requestWebInfo(String cookieString, String uri) {
        String result;
        try{
            result = makeRequestWebInfo(cookieString, uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new CustomException(ErrorCode.SCRAPPER_ERROR);
        }
        if(result == null){
            throw new CustomException(ErrorCode.SCRAPPER_ERROR, "응답값이 존재하지 않습니다.");
        }
        return result;
    }

    protected WebClient.RequestBodySpec makeRequestWebInfo(String cookieString, String uri) {
        return webClient.post()
                .uri(uri)
                .header("Cookie", cookieString);
    }
}
