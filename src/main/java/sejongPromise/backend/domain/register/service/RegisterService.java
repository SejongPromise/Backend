package sejongPromise.backend.domain.register.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sejongPromise.backend.domain.register.RegisterRepository.RegisterRepository;
import sejongPromise.backend.domain.register.model.Register;
import sejongPromise.backend.global.error.ErrorCode;
import sejongPromise.backend.global.error.exception.CustomException;
import sejongPromise.backend.infra.sejong.service.classic.SejongClassicCrawlerService;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService {
    private final RegisterRepository registerRepository;
    private final SejongClassicCrawlerService sejongClassicCrawlerService;

    //todo : 응시가 완료된 시점에서 스케줄링으로 상태값 반환하기
    public void cancelRegister(Long studentId, Long registerId){

        Register register = registerRepository.findById(registerId).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_DATA, "해당 시험을 찾을 수 없습니다.")); //databases에 registerId 존재
        if(studentId.equals(register.getStudent().getId())){
            register.cancelRegister(); //캔슬 호출
            registerRepository.save(register); //다시 저장
        }
        throw new CustomException(ErrorCode.NOT_STUDENT_MATCH);
    }

    //신청하기->가영이가 신청하기 로직 만들면 신청하자마자 CREATEREGISTER 로직 구현하기

}

