package sejongPromise.backend.domain.reserve.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.reserve.model.dto.response.ResponseReserveDto;
import sejongPromise.backend.domain.reserve.repository.ReserveRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService{
    private final ReserveRepository reserveRepository;
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
