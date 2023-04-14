package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.dto.GetCancelDataDto;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SejongClassicCrawlerServiceTwo {
    @ChromeAgentWebClient
    private final WebClient webClient;
    @Value("${sejong.classic.student.schedule}")
    private final String STUDENT_SCHEDULE_URI;
    @Value("${sejong.classic.book.test.cancel}")
    private final String BOOK_REGISTER_CANCEL_URI;

    public String getCancelOPAP(String JSession, GetCancelDataDto dto) {

        String result;

        try {
            result = webClient.get()
                    .uri(STUDENT_SCHEDULE_URI)
                    .header("Cookie", JSession)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return parseOPAPHtml(result, dto);
    }

    public String parseOPAPHtml(String html, GetCancelDataDto dto){
        Document doc = Jsoup.parse(html);
        Elements tableList = doc.select("table[class=listA]").select("tbody");

        for(Element table: tableList) {
            Elements rowList = table.select("tr");

            for (Element row : rowList) {
                Elements cellList = row.select("td");

                //학생 당 한 주에 한번만 시험 신청 가능 -> date 만 비교하면 됨
                String[] dates = cellList.get(1).text().split("-");//"yyyy-MM-dd"
                LocalDate date = LocalDate.of(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]), Integer.parseInt(dates[2]));

                if (dto.getDate().equals(date)) {
                    String[] split = cellList.get(5).select("button").attr("onclick").split("'");//OPAP 값
                    return split[1];
                }
            }
        }
        throw  new CustomException(ErrorCode.NOT_FOUND_DATA); //일치하는 OPAP 값이 없음 -> 오류
    }

    public void cancelRegister(String JSession, String cancelData){
        String result;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("opAppInfoId", cancelData);
        try{
            result = webClient.post()
                    .uri(BOOK_REGISTER_CANCEL_URI)
                    .header("Cookie",JSession)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
    }
}
