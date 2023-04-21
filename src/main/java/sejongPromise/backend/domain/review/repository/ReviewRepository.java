package sejongPromise.backend.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sejongPromise.backend.domain.review.model.Review;


public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r " +
            "where r.book.id=:bookId " +
            "and (r.status='ACTIVE' or r.status='EDITED')")
    Page<Review> findAllByBookId(Long bookId, Pageable pageable);
}
