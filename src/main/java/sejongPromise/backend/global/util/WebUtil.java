package sejongPromise.backend.global.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebUtil {
    public static List<ResponseCookie> extractCookies(HttpHeaders headers){
        List<ResponseCookie> cookies = new ArrayList<>();
        List<String> setCookies = headers.get("Set-Cookie");
        
        if(setCookies == null) return cookies;
        
        for(String value : setCookies){
            String[] blocks = value.split(";");
            String[] cookie = blocks[0].split("=");
            ResponseCookie responseCookie = ResponseCookie.from(cookie[0].trim(), cookie[1].trim()).build();
            cookies.add(responseCookie);
        }
        return cookies;
    }

    public static void addMappedCookies(MultiValueMap<String, String> dest, List<ResponseCookie> src) {
        for(ResponseCookie cookie : src){
            dest.add(cookie.getName(), cookie.getValue());
        }
    }

    public static String makeCookieString(MultiValueMap<String, String> cookies){
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, List<String>>> entries = cookies.entrySet();
        for(Map.Entry<String, List<String>> entry : entries){
            for(String value : entry.getValue()){
                sb.append(entry.getKey()).append("=").append(value).append(";");
            }
        }
        return sb.deleteCharAt(sb.lastIndexOf(";")).toString();
    }


}
