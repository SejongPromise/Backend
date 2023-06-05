package sejongPromise.backend.domain.reserve.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import sejongPromise.backend.domain.reserve.model.dto.response.ResponseReserveDto;
import sejongPromise.backend.domain.reserve.repository.ReserveRepository;

import sejongPromise.backend.infra.sejong.service.portal.ReservingBookService;

import java.util.List;


@Repository
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReserveServiceImpl implements ReserveService{
    private final ReserveRepository reserveRepository;
    private final ReservingBookService reservingBookService;

    @Override
    public void reserve(Long studentId, String title) {
    }

    @Override
    public void cancel(Long studentId, Long reserveId) {

    }

    @Override
    public List<ResponseReserveDto> list(Long studentId) {
        return null;
    }
    @Override
    public String checkStatus(String title) {
        return null;
    }

}
