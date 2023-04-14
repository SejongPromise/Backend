package sejongPromise.backend.infra.sejong.service.classic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;

import java.util.List;

public class SejongRegisterService extends SejongScrapper{
    private final String REGISTER_SCHEDULE_URI;
    private final String REGISTER_BOOK_CODE_URI;
    private final String REGISTER_BOOK_SCHEDULE_URI;
    private final String REGISTER_URI;


    public SejongRegisterService(@ChromeAgentWebClient WebClient webClient,
                                 @Value("${sejong.classic.student.schedule}") String registerScheduleUri,
                                 @Value("${sejong.classic.book.code.list}") String registerBookCodeUri,
                                 @Value("${sejong.classic.book.schedule}") String registerBookScheduleUri,
                                 @Value("${sejong.classic.book.test.register}") String registerUri) {
        super(webClient);
        this.REGISTER_SCHEDULE_URI = registerScheduleUri;
        this.REGISTER_BOOK_CODE_URI = registerBookCodeUri;
        this.REGISTER_BOOK_SCHEDULE_URI = registerBookScheduleUri;
        this.REGISTER_URI = registerUri;
    }

    public List<MyRegisterInfo> crawlRegisterInfo(String cookieString){
        String html = requestWebInfo(cookieString, REGISTER_SCHEDULE_URI);
        return parseRegisterInfo(html);
    }

    private List<MyRegisterInfo> parseRegisterInfo(String html) {
        Document doc = Jsoup.parse(html);
        Elements registerList = doc.select("table[class=listA] tbody");

        for(Element registerInfo : registerList){
            // Skip -> colspan 속성을 가지고 있는 검색 결과가 없는 경우.
            if(registerInfo.select("tr").select("td").hasAttr("colspan")){
                continue;
            }
            Elements rowList = registerInfo.select("tr");
            // 신청 상태 data
            Elements data = rowList.select("td");
            if (data.text().contains("예약취소")) {
                
            }

        }
        return null;
    }

}
