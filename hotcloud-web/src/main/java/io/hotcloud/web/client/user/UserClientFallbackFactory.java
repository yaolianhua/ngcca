package io.hotcloud.web.client.user;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.client.R;
import io.hotcloud.web.client.RP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

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
                return ResponseEntity.ok(R.ok(404, "not found", new User()));
            }

            @Override
            public ResponseEntity<RP<User>> paging(String username, Boolean enabled, Integer page, Integer pageSize) {
                log.error("{}", cause.getCause().getMessage());
                return ResponseEntity.ok(RP.ofSingle(Collections.emptyList()));
            }

            @Override
            public ResponseEntity<R<User>> create(User user) {
                log.error("{}", cause.getCause().getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(R.error(500, cause.getCause().getMessage()));
            }
        };
    }
}
