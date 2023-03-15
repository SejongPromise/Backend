package sejongPromise.backend.infra.sejong.dto;

import lombok.Getter;

@Getter
public class SejongClassicScheduleResponseDto {
    private final String time; //시간
    private final int applicant; //신청자수

    public SejongClassicScheduleResponseDto(String time, int applicant) {
        this.time = time;
        this.applicant = applicant;
    }
}
