package io.hotcloud.web.login;

import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.user.User;
import io.hotcloud.web.feign.CodeMessage;
import io.hotcloud.web.mvc.R;
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
            public ResponseEntity<R<BearerToken>> login(String username, String password) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code))
                        .body(R.error(code, message));
            }

            @Override
            public ResponseEntity<R<User>> retrieveUser() {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code))
                        .body(R.error(code, message));
            }
        };


    }
}
