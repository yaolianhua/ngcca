package io.hotcloud.web.client.login;

import io.hotcloud.security.api.BearerToken;
import io.hotcloud.web.client.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component("LoginClientFallback")
public class LoginClientFallback implements LoginClient {
    @Override
    public ResponseEntity<R<BearerToken>> login(String username, String password) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.error(401, "Unauthorized"));
    }
}
