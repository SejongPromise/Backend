package sejongPromise.backend.infra.sejong.service.portal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

@Slf4j
@RequiredArgsConstructor
public class SejongPortalRequester {

    private final WebClient webClient;
    protected ResponseEntity<String> requestApiByParam(String uri, String param) {
        ResponseEntity<String> response;
        try{
            response = makeRequestApiByParam(uri, param)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new CustomException(ErrorCode.REQUEST_API_ERROR);
        }

        return response;
    }

    protected ResponseEntity<String> requestApiByCookie(String uri, String cookieString) {
        ResponseEntity<String> response;
        try{
            log.info("cookie로 api 생성");
            response = makeRequestApiByCookie(uri, cookieString)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new CustomException(ErrorCode.REQUEST_API_ERROR);
        }
        return response;
    }

    private RequestBodySpec makeRequestApiByParam(String uri, String param) {
        return (RequestBodySpec) webClient.post()
                .uri(uri)
                .header("Origin","https://portal.sejong.ac.kr")
                .header("Host","portal.sejong.ac.kr")
                .header("Referer", "https://portal.sejong.ac.kr/jsp/login/loginSSL.jsp?rtUrl=portal.sejong.ac.kr/comm/member/user/ssoLoginProc.do")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(param));
    }

    private RequestBodySpec makeRequestApiByCookie(String uri, String cookieString) {
        log.info("진짜로 post");
        return (RequestBodySpec) webClient.post()
                .uri(uri)
                .header("Host", "library.sejong.ac.kr")
                .header("Cookie", cookieString);
    }

}
