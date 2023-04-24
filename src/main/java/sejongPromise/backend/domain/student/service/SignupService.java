package sejongPromise.backend.domain.student.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.Role;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.repository.ExamRepository;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.domain.student.model.Student;
import sejongPromise.backend.domain.student.model.dto.request.RequestStudentInfoDto;
import sejongPromise.backend.domain.student.repository.StudentRepository;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.global.util.WebUtil;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        ClassicStudentInfo studentInfo = sejongStudentService.crawlStudentInfo(WebUtil.makeCookieString(auth.cookies));
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
            SejongAuth auth = sejongAuthenticationService.login(studentId.toString(), password);

            //#1. User Session Update
            student.updateSessionToken(WebUtil.makeCookieString(auth.cookies));
            ClassicStudentInfo studentInfo = sejongStudentService.crawlStudentInfo(student.getSessionToken());

            //#2. 학생 정보 Update
            student.updateStudentInfo(studentInfo);

            //#3. 인증 시험 현황 Update
            List<ExamInfo> newExamInfoList = studentInfo.getExamInfoList();
            List<Exam> alreadyExamInfoList = examRepository.findAllByStudentId(student.getId());

            List<Exam> noneMatchList = alreadyExamInfoList.stream()
                    .filter(o -> newExamInfoList.stream().noneMatch( n -> {
                        return o.getExamDate().equals(n.getExamDate());
                    })).collect(Collectors.toList());

            alreadyExamInfoList.addAll(noneMatchList);

            //#4. 시험 완료 후 응시 예정 데이터 삭제
            registerRepository.delete((Register) noneMatchList);

            }else{
                throw new CustomException(ErrorCode.WRONG_PASSWORD);
            }
        }

    }
