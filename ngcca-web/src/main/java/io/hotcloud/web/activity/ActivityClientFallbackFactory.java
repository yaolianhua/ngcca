package io.hotcloud.web.activity;

import io.hotcloud.web.feign.CodeMessage;
import io.hotcloud.web.mvc.PageResult;
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
public class ActivityClientFallbackFactory implements FallbackFactory<ActivityClient> {

    @Override
    public ActivityClient create(Throwable cause) {
        CodeMessage codeMessage = codeMessage(cause);
        int code = codeMessage.getCode();
        String message = codeMessage.getMessage();

        return (user, target, action, page, pageSize) -> {
            log.error("{}", cause.getMessage());
            return ResponseEntity.status(HttpStatus.valueOf(code)).body(PageResult.ofSingle(Collections.emptyList()));
        };
    }
}
