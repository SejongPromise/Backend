package sejongPromise.backend.domain.reserve.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.domain.reserve.model.Reserve;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ResponseReserveDto {

    private final Long id;
    private final String title;
    private final LocalDate reserveDate;
    private final Integer reserveCount;
    private final String status;

    public ResponseReserveDto(Reserve reserve){
        this.id = reserve.getId();
        this.title = reserve.getTitle();
        this.reserveDate = reserve.getReserveDate();
        this.reserveCount = reserve.getReserveCount();
        this.status = reserve.getStatus().toString();
    }

}
