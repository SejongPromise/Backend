package sejongPromise.backend.global.config.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtTestController {

    @PostMapping("/test")
    public ResponseEntity test(){
        return new ResponseEntity(HttpStatus.OK);
    }
}
