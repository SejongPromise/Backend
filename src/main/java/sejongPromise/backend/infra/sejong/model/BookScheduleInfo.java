package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookScheduleInfo {
    private final String time; //시간
    private final int applicant; //신청자수
}
