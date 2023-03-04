package sejongPromise.backend.infra.sejong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@RequiredArgsConstructor
public class SejongClassicAuthenticationService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.classic.login.api-path}")
    private final String loginApiPath;

    public SejongAuth login(String classId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        ClientResponse response = tryLogin(classId, password);

        addMappedCookies(cookies, response.cookies());

        return new SejongAuth(cookies);
    }

    // todo : WebUtil 로 메소드 추출
    private void addMappedCookies(MultiValueMap<String, String> dest, MultiValueMap<String, ResponseCookie> src) {
        Set<Map.Entry<String, List<ResponseCookie>>> cookies = src.entrySet();
        for(Map.Entry<String, List<ResponseCookie>> ent : cookies){
            for(ResponseCookie cookie : ent.getValue()){
                dest.add(ent.getKey(), cookie.getValue());
            }
        }
    }

    private ClientResponse tryLogin(String classId, String password) {
        String param = String.format("userId=%s&password=%s&go=", classId, password);

        ClientResponse response;

        try{
            response = webClient.post()
                    .uri(loginApiPath)
                    .header("Origin","http://classic.sejong.ac.kr")
                    .header("Host","classic.sejong.ac.kr")
                    .header("Referer", "http://classic.sejong.ac.kr/userLoginPage.do")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .exchangeToMono(Mono::just)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        checkJssesionId(response);

        return response;
    }

    private void checkJssesionId(ClientResponse response) {
        if (response != null && !response.cookies().containsKey("JSESSIONID") && !response.statusCode().is3xxRedirection()) {
            throw new RuntimeException("JSEESIONID 존재하지 않음");
        }
    }
}
