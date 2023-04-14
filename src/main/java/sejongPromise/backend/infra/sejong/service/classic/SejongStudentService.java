package sejongPromise.backend.infra.sejong.service.classic;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.infra.sejong.model.ClassicStudentInfo;
import sejongPromise.backend.infra.sejong.model.ExamInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SejongStudentService extends SejongScrapper{
    private final String STUDENT_INFO_URI;

    public SejongStudentService(@ChromeAgentWebClient WebClient webClient,
                                @Value("${sejong.classic.student.info}") String studentInfoUri) {
        super(webClient);
        this.STUDENT_INFO_URI = studentInfoUri;
    }

    public ClassicStudentInfo crawlStudentInfo(String cookieString) {
        String html = requestWebInfo(cookieString, STUDENT_INFO_URI);
        return parseStudentInfoHtml(html);
    }

    private ClassicStudentInfo parseStudentInfoHtml(String html) {
        Document doc = Jsoup.parse(html);
        
        //학생정보 크롤링
        Elements studentInfo = doc.select("div.content-section ul.tblA dd");
        String major = studentInfo.get(0).text();
        String studentId = studentInfo.get(1).text();
        String name = studentInfo.get(2).text();
        String semester = studentInfo.get(5).text();
        boolean isPass = studentInfo.get(7).text().equals("인증");

        //시험 정보 크롤링
        List<ExamInfo> examList = new ArrayList<>();
        Elements examInfoList = doc.select("div.content-section div.table_group tbody tr");
        for(Element element : examInfoList){
            //filtering -> 도서 인증 영역 텍스트를 가지고 있는 Element.
            List<String> fields = Stream.of(BookField.values()).map(BookField::getName).collect(Collectors.toList());

            for(String field : fields){
                Elements elementsContainingText = element.getElementsContainingText(field);

                if(elementsContainingText.hasText()){
                    Elements examInfo = elementsContainingText.select("td");
                    String examYear = examInfo.get(0).text().substring(0,4);
                    String examSemester = examInfo.get(0).text().substring(7);
                    String title;
                    if(examInfo.get(1).text().equals(field)) {
                        title = examInfo.get(2).text();
                    }else{
                        title = examInfo.get(3).text();
                    }
                    boolean examPass = true;
                    String passText = examInfo.select("span.pass").text();
                    if(!passText.isBlank()){
                        examPass = passText.contains("이수") | passText.contains("합격");
                    }
                    ExamInfo exam = new ExamInfo(examYear, examSemester, field, title, examPass);
                    examList.add(exam);
                }
            }
        }

        //시험 정보 중복 제거
        examList = examList.stream().distinct().collect(Collectors.toList());
        return new ClassicStudentInfo(major, studentId, name, semester, isPass, examList);
    }
}
