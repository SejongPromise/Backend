package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SejongClassicAuthenticationService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.classic.login}")
    private final String LOGIN_URI;

    public SejongAuth login(String classId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        ResponseEntity<String> response = tryLogin(classId, password);

        WebUtil.addMappedCookies(cookies, WebUtil.extractCookies(response.getHeaders()));

        return new SejongAuth(cookies);
    }

    private ResponseEntity<String> tryLogin(String classId, String password) {
        String param = String.format("userId=%s&password=%s&go=", classId, password);

        ResponseEntity<String> response;

        try{
            response = webClient.post()
                    .uri(LOGIN_URI)
                    .header("Origin","http://classic.sejong.ac.kr")
                    .header("Host","classic.sejong.ac.kr")
                    .header("Referer", "http://classic.sejong.ac.kr/userLoginPage.do")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        if(response == null) throw new CustomException(ErrorCode.NO_RESPONSE);

        checkJssesionId(response.getHeaders());

        return response;
    }

    private void checkJssesionId(HttpHeaders response) {
        List<ResponseCookie> responseCookies = WebUtil.extractCookies(response);
        if(responseCookies.stream()
                .noneMatch(data -> data.getName().contains("JSESSIONID"))){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "JSession 못 찾음");
        }
    }
}
