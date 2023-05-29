package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.Role;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.repository.ExamRepository;
import sejongPromise.backend.domain.register.repository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.StudentInfo;
import sejongPromise.backend.infra.sejong.model.ExamInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.SejongAuth;
import sejongPromise.backend.infra.sejong.service.classic.SejongAuthenticationService;
import sejongPromise.backend.infra.sejong.service.classic.SejongRegisterService;
import sejongPromise.backend.infra.sejong.service.classic.SejongStudentService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SignupService {
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final RegisterRepository registerRepository;
    private final SejongAuthenticationService sejongAuthenticationService;
    private final SejongStudentService sejongStudentService;
    private final SejongRegisterService sejongRegisterService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 유저 id, pw 로 대양 휴머니티 칼리지 인증 접속을 합니다.
     * 인증 후, 마이페이지에서 학번, 전공, 이름, 학기, 인증여부를 포함한 Student 를 생성하고 저장합니다.
     * 인증을 한번이라도 한 학생은 업데이트가 불가합니다.
     * @param dto 학번, 학과
     * @return 학번, 전공, 이름, 학기
     */
    @Transactional
    public void signup(RequestStudentInfoDto dto){
        //todo: 비밀번호 변경 가능하게 할지 말지
        Optional<Student> optionalStudent = studentRepository.findById(Long.parseLong(dto.getStudentId()));
        if(optionalStudent.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_USER_EXIST);
        }

        //1. 학생 정보 저장
        SejongAuth auth = sejongAuthenticationService.login(dto.getStudentId(), dto.getPassword());
        StudentInfo studentInfo = sejongStudentService.crawlStudentInfo(WebUtil.makeCookieString(auth.cookies));
        Student student = Student.builder()
                .name(studentInfo.getName())
                .major(studentInfo.getMajor())
                .studentId(Long.parseLong(studentInfo.getStudentId()))
                .semester(studentInfo.getSemester().replace(" ", ""))
                .sessionToken(WebUtil.makeCookieString(auth.cookies))
                .pass(studentInfo.isPass())
                .encodedPassword(passwordEncoder.encode(dto.getPassword()))
                .role(Role.STUDENT)
                .build();
        Student saveStudent = studentRepository.save(student);

        //2.시험 정보 저장
        List<ExamInfo> examInfoList = studentInfo.getExamInfoList();
        examInfoList.forEach(data -> {
            Exam exam = Exam.builder().title(data.getTitle())
                    .year(data.getYear())
                    .semester(data.getSemester().replace(" ", ""))
                    .isPass(data.isPass())
                    .field(data.getField())
                    .student(saveStudent)
                    .isTest(data.isTest())
                    .build();
            examRepository.save(exam);
        });

        //3.신청내역과 신청 취소 내역 저장
        List<MyRegisterInfo> myRegisterInfoList = sejongRegisterService.crawlRegisterInfo(WebUtil.makeCookieString(auth.cookies));
        myRegisterInfoList.forEach(data -> {
            Register register = Register.builder()
                    .student(saveStudent)
                    .year(Integer.parseInt(data.getYear()))
                    .semester(Semester.of(data.getSemester()))
                    .date(LocalDate.parse(data.getDate(), DateTimeFormatter.ISO_DATE))
                    .startTime(LocalTime.parse(data.getStartTime(), DateTimeFormatter.ISO_TIME))
                    .endTime(LocalTime.parse(data.getEndTime(), DateTimeFormatter.ISO_TIME))
                    .bookTitle(data.getBookTitle())
                    .cancelOPAP(data.getCancelOPAP())
                    .build();
            registerRepository.save(register);

        });

    }

    @Transactional
    public void refreshSession(Long studentId, String password) {
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 유저를 찾을 수 없습니다.")
        );
        if (passwordEncoder.matches(password, student.getPassword())) {
            // 세션 토큰 갱신
            SejongAuth auth = sejongAuthenticationService.login(studentId.toString(), password);
            student.updateSessionToken(WebUtil.makeCookieString(auth.cookies));

            // 학생 정보 갱신
            StudentInfo studentInfo = sejongStudentService.crawlStudentInfo(student.getSessionToken());
            student.updateStudentInfo(studentInfo);

            // 인증 정보 갱신
            List<Exam> alreadyExamList = examRepository.findAllByStudentId(student.getId());
            List<ExamInfo> updateExamInfoList = studentInfo.getExamInfoList();
            updateExam(student, alreadyExamList, updateExamInfoList);

            // 예약현황 갱신
            List<Register> alreadyRegisterList = registerRepository.findAllByStudentId(student.getId());
            List<MyRegisterInfo> updateRegisterInfoList = sejongRegisterService.crawlRegisterInfo(student.getSessionToken());
            deleteRegister(alreadyRegisterList, updateRegisterInfoList);
        }else{
            throw new CustomException(ErrorCode.WRONG_PASSWORD);
        }
    }

    private void deleteRegister(List<Register> dest, List<MyRegisterInfo> src) {
        dest.forEach(register -> {
            //날짜 비교 해당 날짜 데이터가 없으면 Register 에서 삭제한다.
            if (src.stream().noneMatch(myRegisterInfo -> myRegisterInfo.getDate().equals(register.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))) {
                registerRepository.delete(register);
            }
        });
    }

    private void updateExam(Student student, List<Exam> dest, List<ExamInfo> src) {
        // 새로운 책이 들어 올 경우
        src.forEach(newExam -> {
            if(dest.stream().noneMatch(exam -> exam.getTitle().equals(newExam.getTitle()))){
                Exam exam = Exam.builder().title(newExam.getTitle())
                        .year(newExam.getYear())
                        .semester(newExam.getSemester().replace(" ", ""))
                        .isPass(newExam.isPass())
                        .field(newExam.getField())
                        .student(student)
                        .isTest(newExam.isTest())
                        .build();
                examRepository.save(exam);
            }
        });

        // 기존에 책이 있는 경우
        src.forEach(newExam -> {
                dest.stream().filter(exam -> exam.getTitle().equals(newExam.getTitle())).forEach(exam -> {
                    exam.updateExamInfo(newExam);
            });
        });
    }

}
