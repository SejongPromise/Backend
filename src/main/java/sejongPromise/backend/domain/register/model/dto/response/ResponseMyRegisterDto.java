package sejongPromise.backend.domain.register.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.domain.register.model.Register;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ResponseMyRegisterDto {
    private final Long id;
    private final String year;
    private final String semester;
    private final String date;
    private final String startTime; //10:00
    private final String endTime; //10:10
    private final String bookTitle;

    public ResponseMyRegisterDto(Register register) {
        this.id = register.getId();
        this.year = register.getYear().toString();
        this.semester = register.getSemester().getName();
        this.date = register.getDate().toString();
        this.startTime = register.getStartTime().toString();
        this.endTime = register.getEndTime().toString();
        this.bookTitle = register.getBookTitle();
    }
}
