package sejongPromise.backend.domain.exam.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.global.model.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyRegister extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "register_id")
    private Long registerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private Boolean isCanceled;
    private MyRegister(@NonNull Student student,
                       @NonNull Integer year,
                       @NonNull String semester,
                       @NonNull String field,
                       @NonNull String title,
                       @NonNull Boolean isCanceled){
        this.student = student;

    }

}

