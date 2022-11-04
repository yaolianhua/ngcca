package io.hotcloud.web.statistics;

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
public class StatisticsClientFallbackFactory implements FallbackFactory<StatisticsClient> {

    @Override
    public StatisticsClient create(Throwable cause) {
        CodeMessage codeMessage = codeMessage(cause);
        int code = codeMessage.getCode();
        String message = codeMessage.getMessage();

        return new StatisticsClient() {
            @Override
            public ResponseEntity<Result<Statistics>> statistics(String userid) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, new Statistics()));
            }

            @Override
            public ResponseEntity<PageResult<Statistics>> statistics(Integer page, Integer pageSize) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(PageResult.ofSingle(Collections.emptyList()));
            }
        };
    }
}
