package sejongPromise.backend.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import sejongPromise.backend.global.error.exception.CustomException;


@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * common
     */
    INVALID_RESPONSE(HttpStatus.BAD_REQUEST, "유효하지 않은 응답입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "해당 데이터를 찾을 수 없습니다."),
    NO_RESPONSE(HttpStatus.NOT_FOUND, "응답값이 존재하지 않습니다."),

    /**
     * token Auth
     */
    EXPIRED_TOKEN(HttpStatus.NOT_ACCEPTABLE, "토큰이 만료되었습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    NOT_GRANTED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "접근 토큰이 필요합니다."),

    /**
     * student
     */
    INVALID_STUDENT_INFO(HttpStatus.BAD_REQUEST, "학생 정보가 유효하지 않습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
    ALREADY_USER_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    NOT_STUDENT_MATCH(HttpStatus.NOT_FOUND, "학생 정보가 올바르지 않습니다."),
    INVALID_ACCESS(HttpStatus.BAD_REQUEST, "회원가입할 수 없습니다."),

    /**
     * register
     */
    ALREADY_CANCEL_REGISTER(HttpStatus.CONFLICT, "이미 취소된 시험 신청입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "유효하지 않은 날짜입니다."),
    UNAVAILABLE_SEMESTER(HttpStatus.FORBIDDEN, "이용 불가능한 학기입니다."),
    EXCEED_APPLY(HttpStatus.BAD_REQUEST, "초과된 시험 신청입니다."),

    /**
     * review
     */
    ALREADY_REVIEWED(HttpStatus.CONFLICT, "이미 리뷰를 작성한 시험입니다."),


    /**
     * sejong
     */
    SCRAPPER_ERROR(HttpStatus.BAD_REQUEST, "세종대학교 사이트에서 정보를 가져올 수 없습니다."),
    REQUEST_API_ERROR(HttpStatus.BAD_REQUEST, "세종대학교 API 에서 정보를 가져올 수 없습니다.");



    private final HttpStatus status;
    private final String message;


}
