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
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookCodeInfo;
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
                             @Value("${sejong.classic.book.code}") String bookCodeUri) {
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
     * 책 코드 정보를 가져옵니다.
     * @param fieldCode
     * @return
     */
    public List<BookCodeInfo> crawlBookCode(String fieldCode){
        List<BookCodeInfo> bookCodeInfoList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(String.format(BOOK_CODE_URI, fieldCode)).ignoreContentType(true).get();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(doc.text());
            JSONObject json = (JSONObject) obj;
            JSONArray resultArr = (JSONArray) json.get("results");
            for (Object o : resultArr) {
                JSONObject bookObj = (JSONObject) o;
                BookCodeInfo bookCodeInfo = new BookCodeInfo((String) bookObj.get("bkName"), (Long) bookObj.get("bkCode"));
                bookCodeInfoList.add(bookCodeInfo);
            }
        } catch (IOException | ParseException e ) {
            throw new CustomException(NOT_FOUND_DATA, "책 코드 정보를 가져올 수 없습니다.");
        }
        return bookCodeInfoList;
    }
}
