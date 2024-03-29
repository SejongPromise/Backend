package sejongPromise.backend.domain.register.model;

import lombok.*;
import org.hibernate.annotations.Where;
import sejongPromise.backend.domain.enumerate.RegisterStatus;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'ACTIVE'")
public class Register extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "register_id")
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    private Integer year;
    @Enumerated(EnumType.STRING)
    private Semester semester;
    private LocalDate date;
    private LocalTime startTime; //10:00
    private LocalTime endTime; //10:10
    private String bookTitle;
    private String cancelOPAP; //OPAP 값
    @Enumerated(EnumType.STRING)
    private RegisterStatus status;

    @Builder
    private Register(@NonNull Student student,
                     @NonNull Integer year,
                     @NonNull Semester semester,
                     @NonNull LocalDate date,
                     @NonNull LocalTime startTime,
                     @NonNull LocalTime endTime,
                     @NonNull String bookTitle,
                     @NonNull String cancelOPAP) {
        this.student = student;
        this.year = year;
        this.semester = semester;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
        this.cancelOPAP = cancelOPAP;
        this.status = RegisterStatus.ACTIVE;
    }
}

