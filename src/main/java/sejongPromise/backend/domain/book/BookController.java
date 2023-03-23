package sejongPromise.backend.domain.book;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import java.util.List;

@Tag(name = "BOOK", description = "BOOK API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final SejongClassicCrawlerService service;

    /**
     * 책 정보를 가져옵니다.
     * @return 고전독서 List
     */
    @GetMapping
    public List<BookInfo> getList(){
        return service.crawlBookInfo();
    }
}
