package sejongPromise.backend.domain.exam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.domain.exam.model.Exam;
import sejongPromise.backend.domain.exam.model.dto.response.ResponseExamInfoDto;
import sejongPromise.backend.domain.exam.repository.ExamRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    public List<ResponseExamInfoDto> list(Long studentId) {
        List<Exam> examList = examRepository.findAllByStudentId(studentId);
        return examList.stream().map(ResponseExamInfoDto::new).collect(Collectors.toList());
    }

    public List<ResponseExamInfoDto> list( String field){
        List<Exam> examList = examRepository.findAllByIsPassAndField(true, BookField.of(field));
        return examList.stream().map(ResponseExamInfoDto::new).collect(Collectors.toList());
    }

}