package sejongPromise.backend.infra.sejong.service.classic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static sejongPromise.backend.global.error.ErrorCode.NOT_FOUND_DATA;

@Service
public class SejongBookService extends SejongRequester{
    private final String BOOK_INFO_URI;
    private final String BOOK_CODE_URI;
    private final String BASE_URL = "https://classic.sejong.ac.kr";
    public SejongBookService(@ChromeAgentWebClient WebClient webClient,
                             @Value("${sejong.classic.book.info}") String bookInfoUri,
                             @Value("${sejong.classic.book.code.list") String bookCodeUri) {
        super(webClient);
        this.BOOK_INFO_URI = bookInfoUri;
        this.BOOK_CODE_URI = bookCodeUri;
    }

    /**
     * 책 정보를 가져옵니다.
     * @return 책 정보 리스트
     */
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

    /**
     * 책 고유 번호를 가져옵니다.
     * @param cookieString 인증에 요청에 필요한 토큰
     * @param title 책 제목
     * @param fieldCode 책 영역 코드
     * @return
     */
    public Long findBookCode(String cookieString, String title, String fieldCode){
        String param = String.format("opTermId=TERM-00566&bkAreaCode=%s", fieldCode);
        String html = requestWebInfo(cookieString, BOOK_CODE_URI, param);
        return parseBookCodeList(html, title);
    }

    private long parseBookCodeList(String result, String title){
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(result);

            //obj를 JSONObject에 담음
            JSONObject json = (JSONObject) obj;
            JSONArray resultArr = (JSONArray) json.get("results");

            for (Object o : resultArr) {
                JSONObject bookObj = (JSONObject) o;
                if (bookObj.get("bkName").equals(title)) {
                    return (long) bookObj.get("bkCode");
                }
            }
            throw new CustomException(NOT_FOUND_DATA);
        } catch (ParseException e) {
            throw new CustomException(NOT_FOUND_DATA, "parsing 실패!");
        }
    }
}
