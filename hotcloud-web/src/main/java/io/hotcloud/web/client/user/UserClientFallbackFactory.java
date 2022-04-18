package io.hotcloud.web.client.user;

import io.hotcloud.security.user.model.User;
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
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public ResponseEntity<R<User>> user(String username) {
                log.error("{}", cause.getCause().getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.error(404, "not found"));
            }
        };
    }
}
