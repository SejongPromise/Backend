package sejongPromise.backend.infra.sejong.service.classic;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.infra.sejong.model.dto.request.FindBookCodeRequestDto;
import sejongPromise.backend.infra.sejong.model.dto.request.TestBookScheduleRequestDto;
import sejongPromise.backend.domain.enumerate.BookField;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.*;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static sejongPromise.backend.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SejongClassicCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;
    @Value("${sejong.classic.book.schedule}")
    private final String BOOK_SCHEDULE_URI;
    @Value("${sejong.classic.book.info}")
    private final String BOOK_INFO_URI;
    @Value("${sejong.classic.student.info}")
    private final String STUDENT_INFO_URI;
    @Value("${sejong.classic.student.schedule}")
    private final String STUDENT_SCHEDULE_URI;
    private final String BASE_URL = "http://classic.sejong.ac.kr";
    @Value("${sejong.classic.book.test.register}")
    private final String BOOK_TEST_REGISTER_URI;
    @Value("${sejong.classic.book.code.list}")
    private final String BOOK_CODE_LIST;

    public List<BookInfo> crawlBookInfo(){
        List<BookInfo> bookInfoList = new ArrayList<>();
        try{
            Document doc = Jsoup.connect(BOOK_INFO_URI).get();
            Elements sections = doc.select("div.listTab li");
            sections.forEach(data -> {
                String section = data.text();
                String id = data.id();
                Elements select = doc.select("#" + id);
                select.forEach(bookInfo ->{
                    Elements list = bookInfo.select("ul.book_list li");
                    list.forEach(bookData -> {
                        String title = bookData.select("span.book_tit").text();
                        String writer = bookData.select("span.book_wr").text();
                        String com = bookData.select("span.book_com").text();
                        String image = bookData.select("span.book_img img").attr("src");
                        BookInfo dto = new BookInfo(section, title, writer, com, BASE_URL + image);
                        bookInfoList.add(dto);
                    });
                });
            });
        }catch (IOException e){
            throw new CustomException(NOT_FOUND_DATA, "책 정보를 가져올 수 없습니다.");
        }

        return bookInfoList;
    }

    /**
     * @param auth
     * @param date
     * @return 해당 날짜 예약 스케쥴 현황 리턴
     */
    public List<BookScheduleInfo> getScheduleInfo(SejongAuth auth, String date) {
        // todo : User 로그인 구현되면 저장된 JSESSION으로 접근하도록 수정할 예정
        //JSESSION 없을 시 다시 로그인 하거나 관리자 계정으로 schedule 받아오는거까지 하거나 하기
        //일단 login해서 얻은 SejongAuth로 구현함

        //id 값으로 student 찾고 JSESSION 찾기.. -> domain의 register에서 새로 만들것.

        String result;
        String param = String.format("shDate=%s", date);
        try{
            result = webClient.post()
                    .uri(BOOK_SCHEDULE_URI)
                    .cookies(auth.authCookies())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromValue(param))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        return parseScheduleHtml(result);
    }

    private List<BookScheduleInfo> parseScheduleHtml(String html) {
        List<BookScheduleInfo> scheduleList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tableList = doc.select("table[class=listA]").select("tbody");

        for(Element table: tableList){
            Elements rowList = table.select("tr");

            for(Element row: rowList){
                Elements cellList = row.select("td");
                String time = cellList.get(3).text();
                String applicant = cellList.get(5).text().substring(0, 2).trim();
                String button = cellList.get(7).select("button").attr("onclick");
                int start = button.indexOf("'");
                int end = button.lastIndexOf("'");
                String apply = button.substring(start + 1, end);

                scheduleList.add(new BookScheduleInfo(time, Integer.parseInt(applicant),apply));
            }
        }
        return scheduleList;
    }

    public ClassicStudentInfo getStudentInfo(SejongAuth auth) {
        String html;
        try{
            html = webClient.get()
                    .uri(STUDENT_INFO_URI)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }

        return parseStudentInfoHtml(html);
    }

    private ClassicStudentInfo parseStudentInfoHtml(String html) {
        //todo : 대회 인증 정보 가져오기 및 중복 제거
        //시험 인증 리스트 배열 생성.
        List<ExamInfo> examInfos = new ArrayList<>();

        //학생정보 가져오기
        Document doc = Jsoup.parse(html);
        Elements studentTable = doc.select("div.content-section ul.tblA");
        Elements studentInfo = studentTable.select("dd");
        String major = studentInfo.get(0).text();
        String studentId = studentInfo.get(1).text();
        String name = studentInfo.get(2).text();
        String semester = studentInfo.get(5).text();
        boolean isPass = studentInfo.get(7).text().contains("인증");

        //시험정보 가져오기
        Elements examInfoList = doc.select("div.content-section div.table_group tbody tr");
        for(Element element : examInfoList){
            //filtering -> 도서 인증 영역 텍스트를 가지고 있는 Element 만.
            List<String> fields = Stream.of(BookField.values()).map(BookField::getName).collect(Collectors.toList());

            for(String field : fields){
                Elements elementsContainingText = element.getElementsContainingText(field);

                if(elementsContainingText.hasText()){
                    Elements td = elementsContainingText.select("td");
                    String passAt = td.get(0).text();
                    String fieldName, title;
                    if(fields.contains(td.get(1).text())){
                        fieldName = td.get(1).text();
                        title = td.get(2).text();
                    }else{
                        fieldName = td.get(2).text();
                        title = td.get(3).text();
                    }
                    Integer year = Integer.parseInt(passAt.substring(0, 4));
                    String passSemester = passAt.substring(7);
                    boolean pass = true;
                    String passText = td.select("span.pass").text();
                    if(!passText.isBlank()){
                        pass = passText.contains("이수") | passText.contains("합격");
                    }
                    ExamInfo examInfo = new ExamInfo(year, passSemester, fieldName, title, pass);
                    examInfos.add(examInfo);
                }
            }
        }
        return new ClassicStudentInfo(major, studentId, name, semester, isPass, examInfos);
    }


    public List<MyRegisterInfo> getMyRegisterInfo(SejongAuth auth) {
        String myRegisterInfoHtml;
        try{
            myRegisterInfoHtml = webClient.get()
                    .uri(STUDENT_SCHEDULE_URI)
                    .cookies(auth.authCookies())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return parseMyRegisterInfoHtml(myRegisterInfoHtml);

    }


    public void testRegister(SejongAuth auth, TestBookScheduleRequestDto dto) {
        String result;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("shInfoId", dto.getShInfoId());
        formData.add("opTermId", dto.getOpTermId());
        formData.add("bkAreaCode", dto.getBkAreaCode());
        formData.add("bkCode", dto.getBkCode());
        try {
            result = webClient.post()
                    .uri(BOOK_TEST_REGISTER_URI)
                    .cookies(auth.authCookies())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private List<MyRegisterInfo> parseMyRegisterInfoHtml(String myRegisterInfoHtml) {

        //나의 신청 현황 리스트 생성
        List<MyRegisterInfo> myRegisterInfoList = new ArrayList<>();
        Document doc = Jsoup.parse(myRegisterInfoHtml);
        Elements tableList = doc.select("table[class=listA]").select("tbody");

        for (Element table : tableList) {
            Elements rowList = table.select("tr");

            if(rowList.get(0).text().contains("결과")){
                rowList.remove(0);
            }

            //시험 신청 현황에서 신청 취소 정보 가져오기
           for (Element row : rowList) {
                Elements cellList = row.select("td");

                String year = cellList.get(0).text().substring(0,5);
                String semester = cellList.get(0).text().substring(7);
                String date = cellList.get(1).text();
                String startTime = cellList.get(2).text().substring(0,5);
                String endTime = cellList.get(2).text().substring(8,13);
                String bookTitle = cellList.get(3).text();
                String deleteDate = cellList.get(4).text();

                myRegisterInfoList.add(new MyRegisterInfo(year, semester, date, startTime, endTime, bookTitle, deleteDate));
            }

            }

        return myRegisterInfoList;
    }

    public long findBookCode(SejongAuth auth, FindBookCodeRequestDto dto){
        String result;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("opTermId", "TERM-00566");
        formData.add("bkAreaCode", dto.getAreaCode());

        try{
            result = webClient.post()
                    .uri(BOOK_CODE_LIST)
                    .cookies(auth.authCookies())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return parseBookCodeList(result, dto.getTitle());
    }

    private long parseBookCodeList(String result, String title){
        //todo : Json 파싱 object mapper로 변경
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(result);

            //obj를 JSONObject에 담음
            JSONObject json = (JSONObject) obj;
            JSONArray resultArr = (JSONArray) json.get("results");

            for (int i = 0; i < resultArr.size(); i++) {
                JSONObject bookObj = (JSONObject) resultArr.get(i);
                if (bookObj.get("bkName").equals(title)) {
                    log.info("bookCode: {}", bookObj.get("bkCode"));
                    return (long) bookObj.get("bkCode");
                }
            }
            throw new CustomException(NOT_FOUND_DATA);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}



