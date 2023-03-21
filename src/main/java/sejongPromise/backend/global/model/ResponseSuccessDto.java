package sejongPromise.backend.global.model;

import lombok.Getter;


/**
 * 응답값 통일을 위한 성공 Dto
 * 모든 응답값을 Json 형식으로 반환하기 위하여 요청 처리 성공시 반환할 Dto 가 없을 때 사용한다.
 */
@Getter
public class ResponseSuccessDto {
    private final String message;
    public ResponseSuccessDto(){
        this("ok");
    }
    public ResponseSuccessDto(String message){
        this.message = message;
    }
}
