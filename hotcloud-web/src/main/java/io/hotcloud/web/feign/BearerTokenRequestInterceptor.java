package io.hotcloud.web.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.hotcloud.web.mvc.WebCookie;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BearerTokenRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String authorization = WebCookie.retrieveCurrentHttpServletRequestAuthorization();
        if (StringUtils.hasText(authorization)) {
            requestTemplate.header("Authorization", "Bearer " + authorization);
        }

    }
}
