package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Getter
@RequiredArgsConstructor
public enum ReportType {
    INSULT(0), //모독
    FALSE(1), //거짓말
    SPAM(2), //스팸
    ETC(3); //기타
    private final Integer idx;
    private static final Map<Integer, ReportType> BY_IDX =
            Stream.of(values()).collect(Collectors.toMap(ReportType::getIdx, e -> e));

    public static ReportType of(Integer idx){
        return BY_IDX.get(idx);
    }
}
