package sejongPromise.backend.domain.enumerate;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    /**
     * 활성 상태
     */
    ACTIVE,
    /**
     * 작성자에 의해 수정된 상태
     */
    EDITED,
    /**
     * 작성자에 의해 삭제된 상태
     */
    DELETED,
    /**
     * 관리자에 의해 삭제된 상태
     */
    DELETED_BY_ADMIN;
}
