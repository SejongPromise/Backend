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
public enum BookRatio {
    FIRST(0), MIDDLE(1), LAST(2);
    private final Integer idx;

    private static final Map<Integer, BookRatio> BY_IDX =
            Stream.of(values()).collect(Collectors.toMap(BookRatio::getIdx, e -> e));

    public static BookRatio of(Integer idx){
        if(BY_IDX.get(idx) == null){
            throw new CustomException(ErrorCode.INVALID_REQUEST, "존재하지 않는 IDX 입니다.");
        }
        return BY_IDX.get(idx);
    }
}
