package sejongPromise.backend.domain.book.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sejongPromise.backend.domain.book.model.dto.response.ResponseBookInfoDto;
import sejongPromise.backend.domain.book.service.BookService;

import java.util.List;

@Tag(name = "BOOK", description = "BOOK API 모음")
@RestController // @ResponseBody & @Controller
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    /**
     * 책 정보를 가져옵니다. Field ex) 서양의 역사와 사상
     * @param field 서양의 역사와 사상, 동양의 역사와 사상, 동서양의 문학, 과학 사상
     * @return 고전독서 List
     */
    @GetMapping
    public List<ResponseBookInfoDto> getListByField(@RequestParam(value = "field", required = false) String field){
        return bookService.list(field);
    }

    /**
     * 단일 책 정보를 가져옵니다. By Id
     * @param bookId 요청 Book ID
     * @return 단일 책 정보
     */
    @GetMapping("/{bookId}")
    public ResponseBookInfoDto findOne(@PathVariable Long bookId){
        return bookService.findOne(bookId);
    }
 }
