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
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.StudentInfo;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class SejongCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.student-info.api-path}")
    private final String studentInfoApiPath;

    public StudentInfo crawlStudentInfo(SejongAuth auth){
        String html = requestStudentInfo(auth);
        return parseHtml(html);
    }

    private String requestStudentInfo(SejongAuth auth) {
        String result;
        try{
            result = webClient.post()
                    .uri(studentInfoApiPath)
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
        String name = getElementTextOrNull(doc, "//*[@id=\"baseForm\"]/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[1]");
        String studentId = getElementTextOrNull(doc, "/html/body/div[2]/div/section/form/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[2]");
        String major = getElementTextOrNull(doc, "//*[@id=\"baseForm\"]/div/div[1]/div/div[1]/div[2]/div/div[1]/div/div[1]/span[4]");
        return new StudentInfo(name, studentId, major);

    }


    private String getElementTextOrNull(Document doc, String xPath) {
        return Optional.of(doc.selectXpath(xPath))
                .map(Elements::text)
                .orElseThrow(() -> new RuntimeException("찾는 데이터 없음"));
    }



}
