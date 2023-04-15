package sejongPromise.backend.domain.register.model;

import lombok.*;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Register extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "register_id")
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student;
    private Integer year;
    @Enumerated(EnumType.STRING)
    private Semester semester;
    private LocalDate date;
    private LocalTime startTime; //10:00
    private LocalTime endTime; //10:10
    private String bookTitle;
    @Enumerated(EnumType.STRING)
    private RegisterStatus status; //응시 상태
    private String cancelOPAP; //OPAP 값
    private LocalDateTime deleteDate;

    @Builder
    private Register(@NonNull Student student,
                     @NonNull Integer year,
                     @NonNull Semester semester,
                     @NonNull LocalDate date,
                     @NonNull LocalTime startTime,
                     @NonNull LocalTime endTime,
                     @NonNull String bookTitle,
                     @NonNull RegisterStatus status,
                     String cancelOPAP,
                     LocalDateTime deleteDate){
        this.student = student;
        this.year = year;
        this.semester = semester;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
        this.status = status;
        this.cancelOPAP = cancelOPAP;
        this.deleteDate = deleteDate;
        }
    public void cancelRegister(){
        this.status = RegisterStatus.CANCELED;
        this.deleteDate = LocalDateTime.now();
    }

}

