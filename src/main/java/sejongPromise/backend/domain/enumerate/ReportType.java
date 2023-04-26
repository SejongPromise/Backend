package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;

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
        if(BY_IDX.get(idx) == null){
            throw new CustomException(ErrorCode.INVALID_REQUEST, "존재하지 않는 신고 입니다.");
        }
        return BY_IDX.get(idx);
    }
}
