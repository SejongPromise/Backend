package sejongPromise.backend.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.book.model.Book;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.enumerate.BookStatus;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByStatus(BookStatus status);

    List<Book> findAllByStatusAndField(BookStatus status, BookField field);

    Optional<Book> findByTitle(String title);
}
