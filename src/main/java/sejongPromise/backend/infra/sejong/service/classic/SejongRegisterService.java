package sejongPromise.backend.infra.sejong.service.classic;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import sejongPromise.backend.global.config.qualifier.ChromeAgentWebClient;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.model.BookScheduleInfo;
import sejongPromise.backend.infra.sejong.model.MyRegisterInfo;
import sejongPromise.backend.infra.sejong.model.dto.request.RequestTestApplyDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SejongRegisterService extends SejongRequester{
    private final String REGISTER_SCHEDULE_URI;
    private final String REGISTER_BOOK_SCHEDULE_URI;
    private final String REGISTER_URI;
    private final String CANCEL_REGISTER_URI;
    public SejongRegisterService(@ChromeAgentWebClient WebClient webClient,
                                 @Value("${sejong.classic.student.schedule}") String registerScheduleUri,
                                 @Value("${sejong.classic.book.schedule}") String registerBookScheduleUri,
                                 @Value("${sejong.classic.book.test.register}") String registerUri,
                                 @Value("${sejong.classic.book.test.cancel}") String cancelRegisterUri) {
        super(webClient);
        this.REGISTER_SCHEDULE_URI = registerScheduleUri;
        this.REGISTER_BOOK_SCHEDULE_URI = registerBookScheduleUri;
        this.REGISTER_URI = registerUri;
        this.CANCEL_REGISTER_URI = cancelRegisterUri;
    }

    /**
     * 나의 시험일정을 크롤링 해옵니다.
     * @param cookieString
     * @return
     */
    public List<MyRegisterInfo> crawlRegisterInfo(String cookieString){
        String html = requestWebInfo(cookieString, REGISTER_SCHEDULE_URI);
        return parseRegisterInfo(html);
    }

    /**
     * 시험일정을 크롤링 해옵니다.
     * @param cookieString Jsession
     * @param date 날짜
     * @return
     */
    public List<BookScheduleInfo> crawlBookScheduleInfo(String cookieString, LocalDate date){
        String param = String.format("shDate=%s", date.toString());
        String html = requestWebInfo(cookieString, REGISTER_BOOK_SCHEDULE_URI, param);
        return parseBookScheduleInfo(html);
    }

    /**
     * 시험을 신청합니다.
     * @param cookieString
     * @param dto
     */
    public void registerTest(String cookieString, RequestTestApplyDto dto){
        String param = String.format("shInfoId=%s&opTermId=%s&bkAreaCode=%s&bkCode=%s", dto.getShInfoId(), dto.getOpTermId(), dto.getBkAreaCode(), dto.getBkCode());
        ResponseEntity<String> response = requestApi(cookieString, REGISTER_URI, param);
        /**
         * todo : response 값을 활용하여 에러처리하기.
         */


    }

    /**
     * 시험신청을 취소합니다.
     * @param cookieString
     * @param cancelOPAP
     */
    public void cancelRegister(String cookieString, String cancelOPAP){
        String param = String.format("opAppInfoId=%s", cancelOPAP);
        ResponseEntity<String> response = requestApi(cookieString, CANCEL_REGISTER_URI, param);
        /**
         * todo : response 값을 활용하여 에러처리하기.
         */
    }
    private List<BookScheduleInfo> parseBookScheduleInfo(String html) {
        Document doc =Jsoup.parse(html);
        Elements tableList = doc.select("table[class=listA]").select("tbody");
        if(tableList.select("td").hasAttr("colspan")){
            throw new CustomException(ErrorCode.NOT_FOUND_DATA, "시험 일정이 없습니다");
        }
        List<BookScheduleInfo> ret = new ArrayList<>();
        for(Element table : tableList){
            Elements rowList = table.select("tr");

            for(Element row : rowList){
                Elements cellList = row.select("td");
                String[] yearAndSemester = cellList.get(1).text().split(" ");
                String year = yearAndSemester[0].substring(0, 4);
                String semester = yearAndSemester[1];
                String date = cellList.get(2).text();
                String time = cellList.get(3).text();
                int applicant = Integer.parseInt(cellList.get(5).text().substring(0,2).trim());
                int limitedApplicant = Integer.parseInt(cellList.get(6).text().substring(0,2).trim());
                String buttonText = cellList.get(7).select("button").attr("onclick");
                String apply = buttonText.substring(buttonText.indexOf("'")+1, buttonText.lastIndexOf("'"));
                boolean isAvailable = applicant < limitedApplicant;

                ret.add(BookScheduleInfo.builder()
                        .year(year)
                        .semester(semester)
                        .date(date)
                        .time(time)
                        .applicant(applicant)
                        .limitedApplicant(limitedApplicant)
                        .apply(apply)
                        .isAvailableApply(isAvailable)
                        .build());
            }
        }
        return ret;
    }

    private List<MyRegisterInfo> parseRegisterInfo(String html) {
        Document doc = Jsoup.parse(html);
        Elements registerList = doc.select("table[class=listA] tbody tr");

        List<MyRegisterInfo> ret = new ArrayList<>();
        for(Element registerInfo : registerList){
            // 검색 기록이 없습니다 SKIP.
            if(registerInfo.select("td").hasAttr("colspan")){
                continue;
            }
            if(registerInfo.select("td").text().contains("취소불가")){
                continue;
            }
            // 예약취소 존재하는 경우 -> 현재 예약중 상태로 저장
            if (registerInfo.select("td").text().contains("예약취소")) {
                Elements dataInfo = registerInfo.select("td");
                String[] yearAndSemester = dataInfo.get(0).text().split(" ");
                String year = yearAndSemester[0].substring(0, 4);
                String semester = yearAndSemester[1];
                String date = dataInfo.get(1).text();
                String startTime = dataInfo.get(2).text().substring(0, 5);
                String endTime = dataInfo.get(2).text().substring(8, 13);
                String bookTitle = dataInfo.get(4).text();
                String[] split = dataInfo.get(5).select("button").attr("onclick").split("'");
                String cancelOPAP = split[1];
                log.info("cancelOPAP : {}", cancelOPAP);
                ret.add(new MyRegisterInfo(year, semester, date, startTime, endTime, bookTitle, false, null, cancelOPAP));
            }
            // 예약취소 - 취소된 예약 목록
            else{
                Elements dataInfo = registerInfo.select("td");
                String year = dataInfo.get(0).text().substring(0, 4);
                String semester = dataInfo.get(0).text().substring(7);
                String date = dataInfo.get(1).text();
                String startTime = dataInfo.get(2).text().substring(0, 5);
                String endTime = dataInfo.get(2).text().substring(8, 13);
                String bookTitle = dataInfo.get(3).text();
                String deleteDate = dataInfo.get(4).text();
                ret.add(new MyRegisterInfo(year, semester, date, startTime, endTime, bookTitle, true, deleteDate, null));
            }
        }
        return ret;
    }

}
