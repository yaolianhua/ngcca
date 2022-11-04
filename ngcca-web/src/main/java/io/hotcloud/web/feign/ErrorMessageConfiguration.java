package io.hotcloud.web.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.hotcloud.web.mvc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class ErrorMessageConfiguration {

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new RawErrorDecoder(objectMapper);
    }

    public static class RawErrorDecoder implements ErrorDecoder {

        private final ObjectMapper objectMapper;

        public RawErrorDecoder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Exception decode(String methodKey, Response response) {
            try {
                Result<?> result = objectMapper.readValue(response.body().asInputStream(), Result.class);
                if (result == null || result.getMessage() == null || result.getCode() == 0) {
                    return new HotCloudWebException(500, "系统内部错误");
                }
                return new HotCloudWebException(result.getCode(), result.getMessage());
            } catch (Exception e) {
                log.error("RawErrorDecoder error. {}", e.getMessage());
                return new HotCloudWebException(500, e.getMessage());
            }

        }
    }
}
