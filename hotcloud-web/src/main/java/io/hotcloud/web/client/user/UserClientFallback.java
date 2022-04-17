package io.hotcloud.web.client.user;

import io.hotcloud.security.user.model.User;
import io.hotcloud.web.client.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class UserClientFallback implements UserClient {

    @Override
    public ResponseEntity<R<User>> user(String username) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.error(404, "not found"));
    }
}
