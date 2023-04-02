package sejongPromise.backend.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.book.model.dto.response.ResponseBookInfoDto;
import sejongPromise.backend.domain.book.repository.BookRepository;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.enumerate.BookStatus;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final SejongClassicCrawlerService classicCrawlerService;
    @Transactional
    public void updateList() {
        List<Book> bookList = bookRepository.findAll();
        List<String> alreadyTitleList = bookList.stream().map(Book::getTitle).collect(Collectors.toList());
        List<BookInfo> bookInfoList = classicCrawlerService.crawlBookInfo();
        List<String> updateTitleList = bookInfoList.stream().map(BookInfo::getTitle).collect(Collectors.toList());
        //#1 기존에 있던 책이 사라진 경우
        bookList.forEach(book -> {
            if(!updateTitleList.contains(book.getTitle())){
                book.deprecated();
            }
        });
        // #2 새로운 책이 생긴 경우
        bookInfoList.forEach(book -> {
            if (!alreadyTitleList.contains(book)) {
                Book newBook = Book.builder()
                        .title(book.getTitle())
                        .field(BookField.of(book.getSection()))
                        .writer(book.getWriter())
                        .com(book.getCom())
                        .imageUrl(book.getImageUrl())
                        .build();
                bookRepository.save(newBook);
            }
        });
    }

    public List<ResponseBookInfoDto> list(){
        return bookRepository.findAllByStatus(BookStatus.ACTIVE).stream().map(ResponseBookInfoDto::new).collect(Collectors.toList());
    }
    public List<ResponseBookInfoDto> list(String field){
        return bookRepository.findAllByStatusAndField(BookStatus.ACTIVE, BookField.of(field)).stream().map(ResponseBookInfoDto::new).collect(Collectors.toList());
    }
    public ResponseBookInfoDto findOne(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서가 존재하지 않습니다."));
        return new ResponseBookInfoDto(book);
    }
}
