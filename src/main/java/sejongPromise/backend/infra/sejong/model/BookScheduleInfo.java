package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookScheduleInfo {
    private final String time; //시간
    private final int applicant; //신청자수
    private final String apply; //버튼 SCH0000
    //todo : 신청 가능 여부 추가
}
