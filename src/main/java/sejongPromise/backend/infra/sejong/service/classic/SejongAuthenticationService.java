package sejongPromise.backend.infra.sejong.service.classic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public class SejongAuthenticationService extends SejongRequester{

    private final String LOGIN_URI;

    public SejongAuthenticationService(@ChromeAgentWebClient WebClient webClient,
                                       @Value("${sejong.classic.login}") String loginUri) {
        super(webClient);
        this.LOGIN_URI = loginUri;
    }

    /**
     * 세종대학교 대양 휴머니티 칼리지에 로그인 합니다.
     * @param studentId
     * @param password
     * @return
     */
    public SejongAuth login(String studentId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        ResponseEntity<String> response = tryLogin(studentId, password);

        WebUtil.addMappedCookies(cookies, WebUtil.extractCookies(response.getHeaders()));

        return new SejongAuth(cookies);
    }

    private ResponseEntity<String> tryLogin(String studentId, String password){
        String param = String.format("userId=%s&password=%s&go=", studentId, password);
        ResponseEntity<String> response = requestApi(LOGIN_URI, param);

        if(response.getStatusCode().is2xxSuccessful()){
            throw new CustomException(ErrorCode.INVALID_STUDENT_INFO);
        }

        checkJsessionId(response.getHeaders());

        return response;
    }


    private static void checkJsessionId(HttpHeaders response) {
        List<ResponseCookie> responseCookies = WebUtil.extractCookies(response);
        if(responseCookies.stream()
                .noneMatch(data -> data.getName().contains("JSESSIONID"))){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "JSession 못 찾음");
        }
    }
}
