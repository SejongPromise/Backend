package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class MyRegisterInfo {
    private final String year;
    private final String semester;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String bookTitle;
    private final String deleteDate;
}
