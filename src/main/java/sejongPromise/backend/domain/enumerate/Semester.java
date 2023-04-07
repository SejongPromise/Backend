package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        return BY_LABEL.get(name);
    }
}
