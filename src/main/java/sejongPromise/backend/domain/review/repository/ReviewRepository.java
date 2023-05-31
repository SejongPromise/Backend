package sejongPromise.backend.domain.review.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sejongPromise.backend.domain.review.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r " +
            "where r.book.id=:bookId " +
            "and (r.status='ACTIVE' or r.status='EDITED')")
    Slice<Review> findAllByBookId(Long bookId, Pageable pageable);
    List<Review> findByBookId(Long bookId);

//    @Query("select new sejongPromise.backend.domain.review.dto.ResponseReviewDto(avg(r.score)) " +
//            "from Review r " +
//            "where r.book.id=:bookId" +
//            "group by r.book")
//    Float getScoreAverageByBookId(Long bookId);
//    @Query("select new sejongPromise.backend.domain.review.dto.ResponseReviewDto(collect_list(r.ratio.idx)) " +
//            "from Review r " +
//            "where r.book.id=:bookId" +
//            "group by r.book"+
//            "order by collect_list(r.ratio.idx) desc")
//    List<ResponseReviewDto> getReviewRatioByBookId(Long bookId);
//    @Query("select new sejongPromise.backend.domain.review.dto.ResponseReviewDto(avg(r.volume)) " +
//            "from Review r " +
//            "where r.book.id=:bookId" +
//            "group by r.book")
//    Integer getReviewAverageVolumeByBookId(Long bookId);


}
