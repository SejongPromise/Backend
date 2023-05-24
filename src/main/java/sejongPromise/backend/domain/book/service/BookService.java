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
import sejongPromise.backend.infra.sejong.model.BookCodeInfo;
import sejongPromise.backend.infra.sejong.model.BookInfo;
import sejongPromise.backend.infra.sejong.service.classic.SejongBookService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final SejongBookService sejongBookService;
    @Transactional
    public void updateList() {
        List<Book> bookList = bookRepository.findAll();
        List<String> alreadyTitleList = bookList.stream().map(Book::getTitle).collect(Collectors.toList());
        List<BookInfo> bookInfoList = sejongBookService.crawlBookInfo();
        List<String> updateTitleList = bookInfoList.stream().map(BookInfo::getTitle).collect(Collectors.toList());
        //#1 기존에 있던 책이 사라진 경우
        bookList.forEach(book -> {
            if(!updateTitleList.contains(book.getTitle())){
                book.deprecated();
            }
        });
        // #2 새로운 책이 생긴 경우
        bookInfoList.forEach(book -> {
            if (!alreadyTitleList.contains(book.getTitle())) {
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

        // #3 책 코드 가져오기
        Stream<Integer> fieldStream = Arrays.stream(BookField.values()).map(BookField::getCode);
        fieldStream.forEach(field -> {
            List<Book> books = bookRepository.findAllByStatusAndField(BookStatus.ACTIVE, BookField.of(field));
            List<BookCodeInfo> bookCodeInfos = sejongBookService.crawlBookCode(field.toString());

            // todo: 해당 도서 코드 찾는 로직 언제 깨져도 이상하지 않음. 수정 필요
            books.forEach(book -> {
                bookCodeInfos.forEach(bookCodeInfo -> {
                    if(bookCodeInfo.getTitle().replaceAll(" ", "").contains(book.getTitle().replaceAll(" ", "").substring(0,2))){
                        book.updateCode(bookCodeInfo.getCode());
                        bookRepository.save(book);
                    }
                });
            });
        });
    }

    public List<ResponseBookInfoDto> list(String field){
        if(field == null)
            return list();
        return bookRepository.findAllByStatusAndField(BookStatus.ACTIVE, BookField.of(field)).stream().map(ResponseBookInfoDto::new).collect(Collectors.toList());
    }

    private List<ResponseBookInfoDto> list(){
        return bookRepository.findAllByStatus(BookStatus.ACTIVE).stream().map(ResponseBookInfoDto::new).collect(Collectors.toList());
    }

    public ResponseBookInfoDto findOne(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 도서가 존재하지 않습니다."));
        return new ResponseBookInfoDto(book);
    }
}
