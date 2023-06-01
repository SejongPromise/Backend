package sejongPromise.backend.domain.reserve.service;


import sejongPromise.backend.domain.reserve.model.dto.response.ResponseReserveDto;

import java.util.List;

public interface ReserveService {
    void reserve(Long studentId, String title);
    void cancel(Long studentId, Long reserveId);
    List<ResponseReserveDto> list(Long studentId);

    /**
     * 해당 함수는 도서 예약 가능 여부를 확인하는 함수입니다.
     * @param title
     * @return
     */
    String checkStatus(String title);

}
