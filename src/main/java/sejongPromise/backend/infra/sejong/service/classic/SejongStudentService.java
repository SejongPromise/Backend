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
public class SejongStudentService extends SejongRequester{
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
            boolean isPass = studentInfo.get(7).text().contains("인증") | studentInfo.get(7).text().contains("대체이수");

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
                        if(examInfo.text().contains("미응시") || examInfo.text().contains("미이수")){
                            continue;
                        }
                        String title;
                        Boolean isTest;
                        String examDate;
                        if(examInfo.get(1).text().equals(field)) {
                            title = examInfo.get(2).text();
                            isTest = true;
                            examDate = examInfo.get(3).text();
                        }else{
                            title = examInfo.get(3).text();
                            isTest = false;
                            examDate = null;
                        }
                        //no-pass 불합격인 경우.
                        boolean examPass = !examInfo.select("span.no-pass").hasText();

                        ExamInfo exam = new ExamInfo(field, title, examPass, isTest, examDate);
                        examList.add(exam);
                    }
                }
            }

            //시험 정보 중복 제거
            examList = examList.stream().distinct().collect(Collectors.toList());
            return new ClassicStudentInfo(major, studentId, name, semester, isPass, examList);
        }
}
