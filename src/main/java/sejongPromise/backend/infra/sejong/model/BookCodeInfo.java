package sejongPromise.backend.infra.sejong.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookCodeInfo {
    private final String bkAreaCode;
    private final String bkCode;
    private final String bkName;
    private final int appCount; //응시횟수
    private final String bkStatus; //P/NP
}
