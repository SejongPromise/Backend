package sejongPromise.backend.debug.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class RequestTestCancelDto {
    @NotNull
    private String cancelData;
}
