package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyRegisterInfo {
    private final String year;
    private final String semester;
    private final String date;
    private final String startTime;
    private final String endTime;
    private final String bookTitle;
    private final String cancelOPAP;
}
