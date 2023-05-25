package sejongPromise.backend.domain.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamFieldInfoDto;
import sejongPromise.backend.domain.exam.model.dto.ResponseExamInfoDto;
import sejongPromise.backend.domain.exam.repository.ExamRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    //이수 현황 연도별 정렬
    public List<ResponseExamInfoDto> list(Long studentId) {
        List<Exam> examList = examRepository.findAllByStudentIdOrderByYearDesc(studentId);
        return examList.stream().map(ResponseExamInfoDto::new).collect(Collectors.toList());
    }
    //이수 현황 영역별 정렬
    public List<ResponseExamFieldInfoDto> fieldList(Long studentId){
        return examRepository.findCountGroupByField(studentId);
    }

}