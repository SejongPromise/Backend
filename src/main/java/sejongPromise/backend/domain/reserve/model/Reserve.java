package sejongPromise.backend.domain.reserve.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sejongPromise.backend.domain.enumerate.ReserveStatus;
import sejongPromise.backend.domain.student.model.Student;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reserve {
    @Id
    @GeneratedValue
    @Column(name = "reserve_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
    private String title;
    private LocalDate reserveDate;
    private Integer reserveCount;
    @Enumerated(EnumType.STRING)
    private ReserveStatus status;

    /**
     *
     * @param student
     * @param title 책제목
     * @param reserveCount 예약중인 상태에서만 번호생성
     * @param status 대출가능, 대출불가능, 예약가능
     */
    @Builder
    private Reserve(Student student, String title, Integer reserveCount, ReserveStatus status) {
        this.student = student;
        this.title = title;
        this.reserveDate = LocalDate.now();
        this.reserveCount = reserveCount;
        this.status = status;
    }
}
