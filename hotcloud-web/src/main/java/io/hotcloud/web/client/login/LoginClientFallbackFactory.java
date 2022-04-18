package io.hotcloud.web.client.login;

import io.hotcloud.security.api.BearerToken;
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.error(401, "Unauthorized[" + cause.getMessage() + "]"));
            }
        };
    }
}
