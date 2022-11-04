package io.hotcloud.web.user;

import io.hotcloud.web.feign.CodeMessage;
import io.hotcloud.web.mvc.PageResult;
import io.hotcloud.web.mvc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static io.hotcloud.web.feign.CodeMessage.codeMessage;

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
            public ResponseEntity<Result<User>> findUserByUsername(String username) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, new User()));
            }

            @Override
            public ResponseEntity<PageResult<User>> paging(String username, Boolean enabled, Integer page, Integer pageSize) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(PageResult.ofSingle(Collections.emptyList()));
            }

            @Override
            public ResponseEntity<Result<User>> create(User user) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<User>> findUserById(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, new User()));
            }

            @Override
            public ResponseEntity<Result<Void>> delete(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<User>> update(User user) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<Void>> onOff(String username, Boolean enable) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }
        };
    }
}
