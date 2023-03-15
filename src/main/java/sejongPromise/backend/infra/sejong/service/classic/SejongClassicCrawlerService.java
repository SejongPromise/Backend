package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.dto.SejongClassicScheduleResponseDto;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.model.StudentInfo;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.ArrayList;
import static sejongPromise.backend.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SejongClassicCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${sejong.classic.student.info}")
    private final String STUDENT_INFO_URI;
    @Value("${sejong.classic.book.schedule}")
    private final String BOOK_SCHEDULE_URI;
    @Value("${sejong.classic.student.schedule}")
    private final String STUDENT_SCHEDULE_URI;
    @Value("${sejong.classic.book.register}")
    private final String BOOK_REGISTER_URI;
    @Value("${sejong.classic.book.info}")
    private final String BOOK_INFO_URI;
    private final String BASE_URL = "http://classic.sejong.ac.kr";


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

    public List<BookInfo> crawlBookInfo(){
        List<BookInfo> bookInfoList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(BOOK_INFO_URI).get();
            Elements sections = doc.select("div.listTab li");
            sections.forEach(data -> {
                String section = data.text();
                String id = data.id();
                Elements select = doc.select("#" + id);
                select.forEach(bookInfo ->{
                    Elements list = bookInfo.select("ul.book_list li");
                    list.forEach(bookData -> {
                        String title = bookData.select("span.book_tit").text();
                        String writer = bookData.select("span.book_wr").text();
                        String com = bookData.select("span.book_com").text();
                        String image = bookData.select("span.book_img img").attr("src");
                        BookInfo dto = new BookInfo(section, title, writer, com, BASE_URL + image);
                        bookInfoList.add(dto);
                    });
                });
            });
        }catch (IOException e){
            throw new CustomException(NOT_FOUND_DATA, "책 정보를 가져올 수 없습니다.");
        }

        return bookInfoList;
    }

    private String register(SejongAuth auth, String registerId) {
        String result;
        String param = String.format("menuInfoId=MAIN_02&shInfoId=%s", registerId);
        try{
            result = webClient.get()
                    .uri(BOOK_REGISTER_URI, param)
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
                    .uri(STUDENT_SCHEDULE_URI)
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
                    .uri(BOOK_SCHEDULE_URI)
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

    /**
     * @param auth
     * @param date
     * @return 해당 날짜 예약 스케쥴 현황 리턴
     */
    public ResponseEntity getScheduleInfo(SejongAuth auth, String date) {
        //User 로그인 구현되면 저장된 JSESSION으로 접근하도록 수정할 예정
        //JSESSION 없을 시 다시 로그인 하거나 관리자 계정으로 schedule 받아오는거까지 하거나 하기
        //일단 login해서 얻은 SejongAuth로 구현함

        String result;
        String param = String.format("shDate=%s", date);
        try{
            result = webClient.post()
                    .uri(BOOK_SCHEDULE_URI)
                    .cookies(auth.authCookies())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        List<SejongClassicScheduleResponseDto> scheduleResponseDtoList = parseScheduleHtml(result);
        return new ResponseEntity(scheduleResponseDtoList, HttpStatus.OK);
    }

    private List<SejongClassicScheduleResponseDto> parseScheduleHtml(String html) {
        List<SejongClassicScheduleResponseDto> scheduleList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tableList = doc.select("table[class=listA]").select("tbody");

        for(Element table: tableList){
            Elements rowList = table.select("tr");
            log.info("rowList size: {}", rowList.size());

            for(Element row: rowList){
                Elements cellList = row.select("td");
                String time = cellList.get(3).text();
                String applicant = cellList.get(5).text().substring(0, 2).trim();

                scheduleList.add(new SejongClassicScheduleResponseDto(time, Integer.parseInt(applicant)));
            }
        }
        return scheduleList;
    }

    private String requestStudentCertificationInfo(SejongAuth auth) {
        String result;
        try{
            result = webClient.get()
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

    private String getElementTextOrNull(Document doc, String xPath) {
        return Optional.of(doc.selectXpath(xPath))
                .map(Elements::text)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA));
    }

}
