package io.hotcloud.web.template;

import io.hotcloud.web.feign.CodeMessage;
import io.hotcloud.web.mvc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static io.hotcloud.web.feign.CodeMessage.codeMessage;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class TemplateDefinitionClientFallbackFactory implements FallbackFactory<TemplateDefinitionClient> {

    @Override
    public TemplateDefinitionClient create(Throwable cause) {
        CodeMessage codeMessage = codeMessage(cause);
        int code = codeMessage.getCode();
        String message = codeMessage.getMessage();


        return new TemplateDefinitionClient() {
            @Override
            public ResponseEntity<Result<TemplateDefinition>> create(TemplateDefinition definition) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<TemplateDefinition>> update(TemplateDefinition definition) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }

            @Override
            public ResponseEntity<Result<TemplateDefinition>> findOne(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, new TemplateDefinition()));
            }

            @Override
            public ResponseEntity<Result<List<TemplateDefinition>>> findAll(String name) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, Collections.emptyList()));
            }

            @Override
            public ResponseEntity<Result<List<String>>> classification() {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message, Collections.emptyList()));
            }

            @Override
            public ResponseEntity<Result<Void>> delete(String id) {
                log.error("{}", cause.getMessage());
                return ResponseEntity.status(HttpStatus.valueOf(code)).body(Result.error(code, message));
            }
        };
    }
}
