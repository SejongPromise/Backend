package sejongPromise.backend.global.model;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.io.Serializable;
import java.util.List;

@Getter
public class ResponseSlice<T> implements Serializable {
    private final List<T> content;
    private final boolean hasNext;
    private final boolean isFirst;
    private final boolean isLast;
    private final boolean isEmpty;

    public ResponseSlice(Slice<T> slice) {
        SliceImpl<T> sliceInfo = new SliceImpl<>(slice.getContent(), slice.getPageable(), slice.hasNext());
        this.content = sliceInfo.getContent();
        this.hasNext = sliceInfo.hasNext();
        this.isFirst = sliceInfo.isFirst();
        this.isLast = sliceInfo.isLast();
        this.isEmpty = sliceInfo.isEmpty();
    }
}
