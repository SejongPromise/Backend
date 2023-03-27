package sejongPromise.backend.domain.register.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import sejongPromise.backend.domain.student.model.Student;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class Register {

    @Id
    @GeneratedValue
    private long registerId;

    private boolean isDeleted; //취소여부
    private int year;
    private int semester;
    private LocalDate date;
    private LocalTime startTime; //시작시간
    private LocalTime endTime; //끝시간
    private String bookTitle; //책제목
    private LocalDate deleteDate; //취소일자

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Student student;

    @Builder
    public Register(@NonNull int year,
                    @NonNull int semester,
                    @NonNull LocalDate date,
                    @NonNull LocalTime startTime,
                    @NonNull LocalTime endTime,
                    @NonNull String bookTitle) {
        this.isDeleted = false;
        this.year = year;
        this.semester = semester;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
    }

    private void setDeleteDate(LocalDate deleteDate) {
        this.deleteDate = deleteDate;
        this.isDeleted = true;
    }
}
