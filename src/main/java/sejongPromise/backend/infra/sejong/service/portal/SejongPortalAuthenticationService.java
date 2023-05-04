package sejongPromise.backend.infra.sejong.service.portal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import java.util.List;

@Slf4j
@Service
public class SejongPortalAuthenticationService extends SejongPortalRequester {
    private final String PORTAL_LOGIN_URI;
    private final String LIBRARY_LOGIN_URI;

    public SejongPortalAuthenticationService(@ChromeAgentWebClient WebClient webClient,
                                             @Value("${sejong.portal.login}") String portalLoginUri,
                                             @Value("${sejong.portal.library.login}") String libraryLoginUri) {
        super(webClient);
        this.PORTAL_LOGIN_URI = portalLoginUri;
        this.LIBRARY_LOGIN_URI = libraryLoginUri;
    }

    /**
     * 세종대학교 포털 사이트에 로그인 하고 얻은 ssoToken으로 학술정보원에 로그인합니다.
     * @param studentId
     * @param password
     * @return
     */
    public SejongAuth login(String studentId, String password) {
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();
        String portalCookieToString="";

        ResponseEntity<String> portalResponse = tryPortalLogin(studentId, password);

        //ssotoken만 필요
        List<ResponseCookie> portalCookies = WebUtil.extractCookies(portalResponse.getHeaders());
        
        for (ResponseCookie cookie : portalCookies) {
            if (cookie.getName().equals("ssotoken")) {
                portalCookieToString += cookie;
            }
        }

        ResponseEntity<String> libraryResponse = tryLibraryLogin(portalCookieToString);
        WebUtil.addMappedCookies(cookies, WebUtil.extractCookies(libraryResponse.getHeaders()));

        return new SejongAuth(cookies);
    }

    private ResponseEntity<String> tryPortalLogin(String studentId, String password){
        String param = String.format("mainLogin=Y&rtUrl=portal.sejong.ac.kr%%2Fcomm%%2Fmember%%2Fuser%%2FssoLoginProc.do&id=%s&password=%s", studentId, password);
        ResponseEntity<String> response = requestApiByParam(PORTAL_LOGIN_URI, param);

        checkSsoToken(response.getHeaders());
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new CustomException(ErrorCode.INVALID_STUDENT_INFO);
        }

        return response;
    }

    private ResponseEntity<String> tryLibraryLogin(String cookieString){
        ResponseEntity<String> response = requestApiByCookie(LIBRARY_LOGIN_URI, cookieString);
        checkJsessionId(response.getHeaders());
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new CustomException(ErrorCode.INVALID_STUDENT_INFO);
        }
        return response;
    }


    private static void checkSsoToken(HttpHeaders response) {
        List<ResponseCookie> responseCookies = WebUtil.extractCookies(response);
        if(responseCookies.stream()
                .noneMatch(data -> data.getName().contains("ssotoken"))){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "ssotoken 못 찾음");
        }
    }

    private static void checkJsessionId(HttpHeaders response) {
        List<ResponseCookie> responseCookies = WebUtil.extractCookies(response);
        if(responseCookies.stream()
                .noneMatch(data -> data.getName().contains("JSESSIONID"))){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "JSESSIONID 못 찾음");
        }
    }
}
