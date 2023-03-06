package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

@Service
@RequiredArgsConstructor
public class SejongClassicCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.classic.student-info.api-path}")
    private final String INFO_API;
    @Value("${sejong.classic.schedule-info.api-path}")
    private final String SCHEDULE_API;
    @Value("${sejong.classic.student.schedule-info.api-path}")
    private final String STUDENT_SCHEDULE_API;

    @Value("${sejong.classic.student.register.api-path}")
    private final String REGISTER_API;

    public String crawlStudentCertificationInfo(SejongAuth auth){
        String html = requestStudentCertificationInfo(auth);
        return html;
    }

    public String crawlScheduleInfo(SejongAuth auth, String date){
        String html = requestScheduleInfo(auth, date);
        return html;
    }


    public String crawlStudentScheduleInfo(SejongAuth auth){
        String html = requestStudentScheduleInfo(auth);
        return html;
    }

    public String requestRegistration(SejongAuth auth, String registerId){
        String html = register(auth, registerId);
        return html;
    }

    private String register(SejongAuth auth, String registerId) {
        String result;
        String param = String.format("menuInfoId=MAIN_02&shInfoId=%s", registerId);
        try{
            result = webClient.get()
                    .uri(REGISTER_API, param)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return result;
    }

    private String requestStudentScheduleInfo(SejongAuth auth) {
        String result;
        try{
            result = webClient.get()
                    .uri(STUDENT_SCHEDULE_API)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return result;
    }

    private String requestScheduleInfo(SejongAuth auth, String date) {
        String result;
        String param = String.format("shDate=%s", date);
        try{
            result = webClient.post()
                    .uri(SCHEDULE_API)
                    .cookies(auth.authCookies())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return result;
    }

    private String requestStudentCertificationInfo(SejongAuth auth) {
        String result;
        try{
            result = webClient.get()
                    .uri(INFO_API)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return result;
    }

}
