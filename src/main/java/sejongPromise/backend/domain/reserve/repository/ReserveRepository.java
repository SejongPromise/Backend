package sejongPromise.backend.domain.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sejongPromise.backend.domain.reserve.model.Reserve;

import java.util.List;

public interface ReserveRepository extends JpaRepository<Reserve, Long>{
}
