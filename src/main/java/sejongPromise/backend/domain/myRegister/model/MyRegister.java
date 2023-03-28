package sejongPromise.backend.domain.myRegister.model;

import lombok.*;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyRegister extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "register_id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer year;
    private Integer semester;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String bookTitle;
    private Boolean isCanceled; //취소여부
    private Boolean isDeleted; //취소된
    private LocalDateTime deleteDate;

    @Builder
    private MyRegister(@NonNull Student student,
                       @NonNull Integer year,
                       @NonNull Integer semester,
                       @NonNull LocalDate date,
                       @NonNull LocalTime startTime,
                       @NonNull LocalTime endTime,
                       @NonNull String bookTitle,
                       @NonNull Boolean isCanceled,
                       @NonNull Boolean isDeleted,
                       @NonNull LocalDateTime deleteDate){
        this.student = student;
        this.year = year;
        this.semester = semester;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
        this.isCanceled = isCanceled;
        this.isDeleted = isDeleted;
        this.deleteDate = deleteDate;

    }

}

