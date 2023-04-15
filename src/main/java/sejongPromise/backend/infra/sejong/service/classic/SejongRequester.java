package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

import static org.springframework.web.reactive.function.client.WebClient.*;

@RequiredArgsConstructor
public class SejongRequester {
    private final WebClient webClient;
    protected ResponseEntity<String> requestApi(String uri, String param) {
        ResponseEntity<String> response;
        try{
            response = makeRequestApi(uri, param)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new CustomException(ErrorCode.REQUEST_API_ERROR);
        }
        validateResponse(response);
        return response;
    }

    protected ResponseEntity<String> requestApi(String cookieString, String uri, String param) {
        ResponseEntity<String> response;
        try{
            response = makeRequestApi(cookieString, uri, param)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new CustomException(ErrorCode.REQUEST_API_ERROR);
        }
        validateResponse(response);
        return response;
    }

    protected String requestWebInfo(String cookieString, String uri, String param) {
        String result;
        try{
            result = makeRequestWebInfo(cookieString, uri, param)
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
    private RequestBodySpec makeRequestApi(String uri, String param) {
        return (RequestBodySpec) webClient.post()
                .uri(uri)
                .header("Origin","http://classic.sejong.ac.kr")
                .header("Host","classic.sejong.ac.kr")
                .header("Referer", "http://classic.sejong.ac.kr/userLoginPage.do")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(param));
    }
    private RequestBodySpec makeRequestApi(String cookieString, String uri, String param) {
        return (RequestBodySpec) webClient.post()
                .uri(uri)
                .header("Origin","http://classic.sejong.ac.kr")
                .header("Host","classic.sejong.ac.kr")
                .header("Referer", "http://classic.sejong.ac.kr/userLoginPage.do")
                .header("Cookie", cookieString)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(param));
    }

    private RequestBodySpec makeRequestWebInfo(String cookieString, String uri) {
        return webClient.post()
                .uri(uri)
                .header("Cookie", cookieString);
    }

    private RequestBodySpec makeRequestWebInfo(String cookieString, String uri, String param) {
        return (RequestBodySpec) webClient.post()
                .uri(uri)
                .header("Cookie", cookieString)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(param));
    }


    private static void validateResponse(ResponseEntity<String> response) {
        if(response == null) throw new CustomException(ErrorCode.NO_RESPONSE);
    }

}
