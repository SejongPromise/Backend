package sejongPromise.backend.infra.sejong.service.portal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.StudentInfo;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class SejongCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.portal.student.info}")
    private final String STUDENT_INFO_URI;
    private final String USER_NAME_XPATH = "/html/body/div[2]/div/section/form/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[1]";
    private final String USER_STUDENT_ID_XPATH = "/html/body/div[2]/div/section/form/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[2]";
    private final String USER_MAJOR_XPATH = "/html/body/div[2]/div/section/form/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[4]";


    public StudentInfo crawlStudentInfo(SejongAuth auth){
        String html = requestStudentInfo(auth);
        return parseHtml(html);
    }

    private String requestStudentInfo(SejongAuth auth) {
        String result;
        try{
            result = webClient.post()
                    .uri(STUDENT_INFO_URI)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        return result;
    }


    private StudentInfo parseHtml(String html) {
        Document doc = Jsoup.parse(html);

        String name = getElementTextOrNull(doc, USER_NAME_XPATH);
        String studentId = getElementTextOrNull(doc, USER_STUDENT_ID_XPATH);
        String major = getElementTextOrNull(doc, USER_MAJOR_XPATH);
        return new StudentInfo(name, studentId, major);

    }


    private String getElementTextOrNull(Document doc, String xPath) {
        return Optional.of(doc.selectXpath(xPath))
                .map(Elements::text)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
    }
}
