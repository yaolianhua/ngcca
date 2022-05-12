package io.hotcloud.web.user;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.CodeMessage;
import io.hotcloud.web.R;
import io.hotcloud.web.RP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static io.hotcloud.web.CodeMessage.codeMessage;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        CodeMessage codeMessage = codeMessage(cause);
        int code = codeMessage.getCode();
        String message = codeMessage.getMessage();
        return new UserClient() {
            @Override
            public ResponseEntity<R<User>> findUserByUsername(String username) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message, new User()));
            }

            @Override
            public ResponseEntity<RP<User>> paging(String username, Boolean enabled, Integer page, Integer pageSize) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(RP.ofSingle(Collections.emptyList()));
            }

            @Override
            public ResponseEntity<R<User>> create(User user) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message));
            }

            @Override
            public ResponseEntity<R<User>> findUserById(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message, new User()));
            }

            @Override
            public ResponseEntity<R<Void>> delete(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message));
            }

            @Override
            public ResponseEntity<R<User>> update(User user) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message));
            }

            @Override
            public ResponseEntity<R<Void>> onOff(String username, Boolean enable) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(R.error(code, message));
            }
        };
    }
}
