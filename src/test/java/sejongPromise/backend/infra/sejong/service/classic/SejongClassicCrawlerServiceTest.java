package sejongPromise.backend.infra.sejong.service.classic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.util.YamlProperties;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SejongClassicCrawlerServiceTest {

    private static YamlProperties properties;
    private SejongClassicCrawlerService service;

    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach(){
        WebClient webClient = WebClient.create();
        String studentInfo = properties.get("sejong.classic.student.info");
        String bookSchedule = properties.get("sejong.classic.book.schedule");
        String studentSchedule = properties.get("sejong.classic.student.schedule");
        String bookRegister = properties.get("sejong.classic.book.register");
        String bookInfo = properties.get("sejong.classic.book.info");
        String myRegisterInfo = properties.get("sejong.classic.stduent.schedule");
        System.out.println("bookInfo = " + bookInfo);
        service = new SejongClassicCrawlerService(webClient, bookSchedule, bookInfo, studentInfo,myRegisterInfo);
    }


    @Test
    @DisplayName("책 정보를 잘 가져오는가?")
    public void getBookInfo(){
        List<BookInfo> bookInfoList = service.crawlBookInfo();
        BookInfo bookInfo = bookInfoList.get(0);
        String title = bookInfo.getTitle();
        assertThat(title).isEqualTo("플라톤의 국가");
    }

}