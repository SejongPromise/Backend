package sejongPromise.backend.infra.sejong.service.portal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

        ClientResponse response = tryLogin(classId, password);
        addMappedCookies(cookies, response.cookies());

        response = trySSOAuth(cookies);
        addMappedCookies(cookies, response.cookies());

        return new SejongAuth(cookies);
    }

    private ClientResponse tryLogin(String classId, String password) {
        String param = String.format("mainLogin=Y&rtUrl=portal.sejong.ac.kr&ssoLoginProc.do&id=%s&password=%s", classId, password);

        ClientResponse response;

        try{
            response = webClient.post()
                    .uri(loginApiPath)
                    .header("Origin", "https://portal.sejong.ac.kr")
                    .header("Referer","https://portal.sejong.ac.kr/jsp/login/loginSSL.jsp")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .exchangeToMono(Mono::just)
                    .block();

        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        checkSSOToken(response);

        return response;
    }

    private ClientResponse trySSOAuth(MultiValueMap<String, String> cookies) {
        ClientResponse response;
        String cookieString = makeCookieString(cookies);
        try{
            response = webClient.get()
                    .uri(ssoApiPath)
//                    todo : Cookie String으로 넣지 않으면 인증이 안됨.. why...?
                    .header("Cookie", cookieString)
//                    .cookies(map -> map.addAll(cookies))
                    .exchangeToMono(Mono::just)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        validateResponse(response);
        validateStatusCode(response.statusCode());

        return response;
    }

    private String makeCookieString(MultiValueMap<String, String> cookies) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, List<String>>> entries = cookies.entrySet();
        for(Map.Entry<String, List<String>> entry : entries){
            sb.append(entry.getKey()).append("=");
            for(String val : entry.getValue()){
                sb.append(val).append(";");
            }
        }
        return sb.deleteCharAt(sb.lastIndexOf(";")).toString();
    }

    private void validateStatusCode(HttpStatus statusCode) {
        if(statusCode != HttpStatus.FOUND){
            System.out.println("statusCode = " + statusCode);
            throw new RuntimeException("리다이렉트 안 뜸");
        }
    }

    private void validateResponse(ClientResponse response) {
        if(response == null){
            throw new RuntimeException("응답값 없음");
        }
    }

    private void addMappedCookies(MultiValueMap<String, String> dest, MultiValueMap<String, ResponseCookie> src) {
        Set<Map.Entry<String, List<ResponseCookie>>> cookies = src.entrySet();
        for(Map.Entry<String, List<ResponseCookie>> ent : cookies){
            for (ResponseCookie value : ent.getValue()) {
                dest.add(ent.getKey(), value.getValue());
            }
        }
    }

    private void checkSSOToken(ClientResponse response) {
        if(response != null && !response.cookies().containsKey("ssotoken")){
            throw new RuntimeException("토큰 존재하지 않음");
        }
    }
}
