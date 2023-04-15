package sejongPromise.backend.domain.exam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sejongPromise.backend.domain.enumerate.BookField;

@Getter
public class ResponseExamFieldInfoDto {
    @Schema(description = "필드", example = "동양의 역사")
    private final String field;

    @Schema(description = "통과 권수", example = "4")
    private final Long passCount;

    public ResponseExamFieldInfoDto(BookField field, Long passCount){
        this.field = field.getName();
        this.passCount = passCount;
    }
}
