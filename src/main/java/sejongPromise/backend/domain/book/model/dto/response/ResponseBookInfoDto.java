package sejongPromise.backend.domain.book.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.domain.book.model.Book;

@RequiredArgsConstructor
@Getter
public class ResponseBookInfoDto {
    private final Long bookId;
    private final String title;
    private final String field;
    private final String writer;
    private final String com;
    private final String imageUrl;
    public ResponseBookInfoDto(Book book){
        this.bookId = book.getId();
        this.title = book.getTitle();
        this.field = book.getField().getName();
        this.writer = book.getWriter();
        this.com = book.getCom();
        this.imageUrl = book.getImageUrl();
    }
}
