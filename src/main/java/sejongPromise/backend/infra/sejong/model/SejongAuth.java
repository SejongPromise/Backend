package sejongPromise.backend.infra.sejong.model;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.function.Consumer;

public class SejongAuth {
    public final MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

    public SejongAuth(MultiValueMap<String, String> cookies){
        this.cookies.addAll(cookies);
    }

    public Consumer<MultiValueMap<String, String>> authCookies() {
        return map -> map.addAll(cookies);
    }

}
