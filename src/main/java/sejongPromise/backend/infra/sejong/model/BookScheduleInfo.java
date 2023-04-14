package sejongPromise.backend.infra.sejong.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.domain.enumerate.Semester;

@Getter
@RequiredArgsConstructor
@Builder
public class BookScheduleInfo {
    private final Integer year;
    private final String semester;
    private final String time; //시간
    private final Integer applicant; //신청자수
    private final Integer limitedApplicant; //제한인원
    private final String apply; //버튼 SCH0000
    private final boolean isAvailableApply; //신청 가능 여부
}
