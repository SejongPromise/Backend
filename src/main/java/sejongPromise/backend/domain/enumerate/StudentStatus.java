package sejongPromise.backend.domain.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum StudentStatus {

    Active,
    Deleted,
    DeletedByAdmin

}

