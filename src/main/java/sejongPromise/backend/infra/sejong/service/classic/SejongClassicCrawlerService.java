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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.domain.enumerate.Semester;
import sejongPromise.backend.domain.register.model.dto.request.RequestFindBookCodeDto;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;
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
    private final String BOOK_SCHEDULE_URI; //REGISTER_
    @Value("${sejong.classic.book.info}")
    private final String BOOK_INFO_URI;
    @Value("${sejong.classic.student.schedule}")
    private final String STUDENT_SCHEDULE_URI; //REGISTER_SCHEDULE
    private final String BASE_URL = "http://classic.sejong.ac.kr";
    @Value("${sejong.classic.book.test.register}")
    private final String BOOK_TEST_REGISTER_URI;
    @Value("${sejong.classic.book.code.list}")
    private final String BOOK_CODE_LIST;

    private final String COOKIE = "Cookie";

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
     * @param JSession
     * @param date
     * @return 해당 날짜 예약 스케쥴 현황 리턴
     */
    public List<BookScheduleInfo> getScheduleInfo(String JSession, String date) {
        String result;
        String param = String.format("shDate=%s", date);
        String[] splitJSession = JSession.split("=");
        try{
            result = webClient.post()
                    .uri(BOOK_SCHEDULE_URI)
                    .cookie(splitJSession[0],splitJSession[1])
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
                String[] yearAndSemester = cellList.get(1).text().split(" ");
                int year = Integer.parseInt(yearAndSemester[0].substring(0, 4));
                String firstLetterSemester = yearAndSemester[1].substring(0, 1);
                //semester 계산
                String semester;
                if (firstLetterSemester.equals("1")) {
                    semester = Semester.FIRST.getName();
                } else if (firstLetterSemester.equals("2")){
                    semester = Semester.SECOND.getName();
                } else if (firstLetterSemester.equals("여")) {
                    semester = Semester.SUMMER.getName();
                } else {
                    semester = Semester.WINTER.getName();
                }

                String time = cellList.get(3).text();
                int applicant = Integer.parseInt(cellList.get(5).text().substring(0, 2).trim());
                int limitedApplicant = Integer.parseInt(cellList.get(6).text().substring(0, 2).trim());
                String button = cellList.get(7).select("button").attr("onclick");
                int start = button.indexOf("'");
                int end = button.lastIndexOf("'");
                String apply = button.substring(start + 1, end);

                Boolean isAvailableApply;
                if ((limitedApplicant - applicant) > 0) {
                    isAvailableApply = true;
                } else {
                    isAvailableApply = false;
                }

                scheduleList.add(BookScheduleInfo.builder()
                        .year(year)
                        .semester(semester)
                        .time(time)
                        .applicant(applicant)
                        .limitedApplicant(limitedApplicant)
                        .apply(apply)
                        .isAvailableApply(isAvailableApply)
                        .build());
            }
        }
        return scheduleList;
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


    public void testRegister(String JSession, RequestTestApplyDto dto) {
        ResponseEntity<String> result;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("shInfoId", dto.getShInfoId());
        formData.add("opTermId", dto.getOpTermId());
        formData.add("bkAreaCode", dto.getBkAreaCode());
        formData.add("bkCode", dto.getBkCode());

        String[] splitJsession = JSession.split("=");
        try {
            result = webClient.post()
                    .uri(BOOK_TEST_REGISTER_URI)
                    .cookie(splitJsession[0],splitJsession[1])
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        System.out.println("result = " + result.getStatusCode().toString());
        /**
         * String 으로 결과 값 받지 않고, Response를 직접 받아서 만약 304.. 성공시 redirect 면 통과시키고
         * 200번대 요청으로 로그인 요청 페이지 뜨면 실패시키고,,
         *
         * 아니면 응답값 확인해서 로그인 응답값을 저장해두고 일치할 경우 Error 반환.
         *
         * redirect까지 진행해야함.
         * redirect까지 진행하고, 응답값에서 "인증 가능 학기가 아닙니다. 관리자에게 문의하시기 바랍니다." -> Text 포함 여부로 에러처리하기
         */

    }

    private List<MyRegisterInfo> parseMyRegisterInfoHtml(String myRegisterInfoHtml) {

        //나의 신청 현황 리스트 생성
        List<MyRegisterInfo> myRegisterInfoList = new ArrayList<>();
        Document doc = Jsoup.parse(myRegisterInfoHtml);
        Elements tableList = doc.select("table[class=listA]").select("tbody");

        for(Element table : tableList){
            // Skip -> colspan 속성을 가지고 있는 검색 결과가 없습니다.
            if(table.select("tr").select("td").hasAttr("colspan")){
                continue;
            }
            Elements rowList = table.select("tr");
            //신청 상태
            //todo : 가영이한테 물어보기! 굉장히 맘에 안듦..
            if(rowList.select("td").text().contains("예약취소")){
                for(Element row : rowList){
                    Elements cellList = row.select("td");
                    String year = cellList.get(0).text().substring(0,4);
                    String semester = cellList.get(0).text().substring(7);
                    String date = cellList.get(1).text();
                    String startTime = cellList.get(2).text().substring(0,5);
                    String endTime = cellList.get(2).text().substring(8,13);
                    String bookTitle = cellList.get(4).text();
                    myRegisterInfoList.add(new MyRegisterInfo(year, semester, date, startTime, endTime, bookTitle, false, null));
                }
            }
            //신청 취소
            else{
                for(Element row : rowList){
                    Elements cellList = row.select("td");
                    String year = cellList.get(0).text().substring(0,4);
                    String semester = cellList.get(0).text().substring(7);
                    String date = cellList.get(1).text();
                    String startTime = cellList.get(2).text().substring(0,5);
                    String endTime = cellList.get(2).text().substring(8,13);
                    String bookTitle = cellList.get(3).text();
                    String deleteDate = cellList.get(4).text();
                    myRegisterInfoList.add(new MyRegisterInfo(year, semester, date, startTime, endTime, bookTitle, true, deleteDate));
                }
            }
        }
        return myRegisterInfoList;
    }

    public long findBookCode(String cookieString, RequestFindBookCodeDto dto){
        String result;

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("opTermId", "TERM-00566");
        formData.add("bkAreaCode", dto.getBookAreaCode());

        try{
            result = webClient.post()
                    .uri(BOOK_CODE_LIST)
                    .header(COOKIE, cookieString)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Throwable t){
            throw new RuntimeException(t);
        }
        return parseBookCodeList(result, dto.getBookTitle());
    }

    private long parseBookCodeList(String result, String title){
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



