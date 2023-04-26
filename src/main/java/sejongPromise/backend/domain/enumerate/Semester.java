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
public enum Semester {
    ZERO("0학기"),
    FIRST("1학기"),
    SECOND("2학기"),
    THIRD("3학기"),
    FOURTH("4학기"),
    FIFTH("5학기"),
    SIXTH("6학기"),
    SEVENTH("7학기"),
    EIGHTH("8학기"),
    NINTH("9학기"),
    TENTH("10학기"),
    SUMMER("여름학기"),
    WINTER("겨울학기");

    private final String name;
    private static final Map<String, Semester> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(Semester::getName, e -> e));

    public static Semester of(String name){
        if(BY_LABEL.get(name) == null){
            throw new CustomException(ErrorCode.INVALID_REQUEST, "존재하지 않는 학기입니다.");
        }
        return BY_LABEL.get(name);
    }

    public boolean isAvailableSemester() { //enum 상수가 더이상 변경되지 않을 것 같아서 ordinal 사용
        return this.ordinal() <= 6;
    }
}
