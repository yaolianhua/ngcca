package io.hotcloud.web.client.login;

import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.user.User;
import io.hotcloud.web.client.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class LoginClientFallbackFactory implements FallbackFactory<LoginClient> {

    @Override
    public LoginClient create(Throwable cause) {
        return new LoginClient() {
            @Override
            public ResponseEntity<R<BearerToken>> login(String username, String password) {
                log.error("{}", cause.getCause().getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.error(500, cause.getMessage()));
            }

            @Override
            public ResponseEntity<R<User>> retrieveUser() {
                log.error("{}", cause.getCause().getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.error(500, cause.getMessage()));
            }
        };


    }
}
