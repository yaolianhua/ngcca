package io.hotcloud.web.login;

import io.hotcloud.web.feign.CodeMessage;
import io.hotcloud.web.mvc.Result;
import io.hotcloud.web.mvc.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static io.hotcloud.web.feign.CodeMessage.codeMessage;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class LoginClientFallbackFactory implements FallbackFactory<LoginClient> {

    @Override
    public LoginClient create(Throwable cause) {
        CodeMessage codeMessage = codeMessage(cause);
        int code = codeMessage.getCode();
        String message = codeMessage.getMessage();
        return new LoginClient() {
            @Override
            public ResponseEntity<Result<BearerToken>> login(String username, String password) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code))
                        .body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<User>> retrieveUser() {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code))
                        .body(Result.error(code, message));
            }
        };


    }
}
