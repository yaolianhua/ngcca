package io.hotcloud.web.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.hotcloud.web.mvc.R;
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
                R<?> r = objectMapper.readValue(response.body().asInputStream(), R.class);
                return new HotCloudWebException(r.getCode(), r.getMessage());
            } catch (Exception e) {
                log.error("RawErrorDecoder error. {}", e.getMessage());
                return new HotCloudWebException(500, e.getMessage());
            }

        }
    }
}
