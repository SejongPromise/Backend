package sejongPromise.backend.infra.sejong.service.portal;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.domain.enumerate.ReserveStatus;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.service.classic.SejongRequester;


import java.io.IOException;

import static sejongPromise.backend.global.error.ErrorCode.NOT_FOUND_DATA;

@Slf4j
@Service
public class ReservingBookService extends SejongRequester {

    private final String BOOK_RESERVE_URI;

    public ReservingBookService(@ChromeAgentWebClient WebClient webClient,
                                @Value("${sejong.portal.library.reserve}") String bookReserveUri) {
        super(webClient);
        this.BOOK_RESERVE_URI = bookReserveUri;
    }

//도서 대출 현황 크롤링
    public ReserveStatus crawlReserveStatusInfo(String title) {

        final ReserveStatus[] status = {ReserveStatus.UNAVAILABLE};

        try {
            Document doc = Jsoup.connect(String.format(BOOK_RESERVE_URI, title)).get();
            Elements bookList = doc.select("div#result_view li");

            for (Element bookInfo : bookList){
                String reserveStatus  = bookInfo.select(" div.body p.tag").text();

                if (reserveStatus.contains("대출가능")) {
                    status[0] = ReserveStatus.AVAILABLE;
                    break;
                } else if (reserveStatus.contains("대출중")) {
                    status[0] = ReserveStatus.RESERVED;
                }
            }
        }catch (IOException e) {
            throw new CustomException(NOT_FOUND_DATA, "책 정보를 가져올 수 없습니다.");
        }
        return status[0];
    }
}