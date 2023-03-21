package sejongPromise.backend.domain.exam.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum BookField {
    W_HISTORY("서양의 역사와 사상"),
    E_HISTORY("동양의 역사와 사상"),
    EW_CULTURE("동서양의 문학"),
    SCIENCE("과학 사상");

    private final String name;

    private static final Map<String, BookField> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(BookField::getName, e -> e));

    public static BookField of(String name){
        return BY_LABEL.get(name);
    }
}
