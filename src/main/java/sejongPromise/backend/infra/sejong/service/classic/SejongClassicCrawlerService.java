package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            Document doc = Jsoup.connect("http://classic.sejong.ac.kr/info/MAIN_02_03.do").get();
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

}
