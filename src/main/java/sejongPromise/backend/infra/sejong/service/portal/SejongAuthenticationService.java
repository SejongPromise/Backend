package sejongPromise.backend.infra.sejong.service.portal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SejongAuthenticationService {
    //todo : Error Response 존재하는지..?
    private static final Pattern ERROR_ALERT_PATTERN = Pattern.compile("");
    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.login.api-path}")
    private final String loginApiPath;

    @Value("${sejong.ssoLogin.api-path}")
    private final String ssoApiPath;

    /**
     * 세종대학교 통합 시스템에 로그인합니다.
     * @param classId  아이디 (학번)
     * @param password 비밀번호
     * @return  SSO 토큰이 포함된 SejongAuth
     */
    public SejongAuth login(String classId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        ResponseEntity<String> response = tryLogin(classId, password);

        WebUtil.addMappedCookies(cookies, WebUtil.extractCookies(response.getHeaders()));

        response = trySSOAuth(cookies);
        WebUtil.addMappedCookies(cookies, WebUtil.extractCookies(response.getHeaders()));

        return new SejongAuth(cookies);
    }

    private ResponseEntity<String> tryLogin(String classId, String password) {
        String param = String.format("mainLogin=Y&rtUrl=portal.sejong.ac.kr&ssoLoginProc.do&id=%s&password=%s", classId, password);

        ResponseEntity<String> response;

        try{
            response = webClient.post()
                    .uri(loginApiPath)
                    .header("Origin", "https://portal.sejong.ac.kr")
                    .header("Referer","https://portal.sejong.ac.kr/jsp/login/loginSSL.jsp")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        assert response != null : "응답값 존재하지 않음";

        checkSSOToken(response.getHeaders());

        return response;
    }

    private ResponseEntity<String> trySSOAuth(MultiValueMap<String, String> cookies) {
        ResponseEntity<String> response;
        try{
            response = webClient.get()
                    .uri(ssoApiPath)
//                    todo : Cookie String으로 넣지 않으면 인증이 안됨.. why...?
                    .header("Cookie", WebUtil.makeCookieString(cookies))
//                    .cookies(map -> map.addAll(cookies))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        if(response == null){
            throw new RuntimeException("응답값 없음");
        }

        if(!response.getStatusCode().is3xxRedirection()){
            throw new RuntimeException("리다이렉션 등록 안됨");
        }

        return response;
    }

    private void checkSSOToken(HttpHeaders headers) {
        List<ResponseCookie> responseCookies = WebUtil.extractCookies(headers);
        if(responseCookies.stream()
                .noneMatch(data -> data.getName().contains("ssotoken"))){
            throw new RuntimeException("ssotoken 존재하지 않음");
        }
    }

}
