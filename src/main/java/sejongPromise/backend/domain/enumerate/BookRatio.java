package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BookRatio {
    FIRST(0), MIDDlE(1), Last(2);
    private final Integer idx;

    private static final Map<Integer, BookRatio> BY_IDX =
            Stream.of(values()).collect(Collectors.toMap(BookRatio::getIdx, e -> e));

    public static BookRatio of(Integer idx){
        return BY_IDX.get(idx);
    }
}
