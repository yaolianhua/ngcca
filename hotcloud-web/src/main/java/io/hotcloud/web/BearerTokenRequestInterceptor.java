package io.hotcloud.web;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yaolianhua789@gmail.com
 **/
public class BearerTokenRequestInterceptor implements RequestInterceptor {

    private final HttpServletRequest request;
    private final ClientAuthorizationManager authorizationManager;

    public BearerTokenRequestInterceptor(HttpServletRequest request,
                                         ClientAuthorizationManager authorizationManager) {
        this.request = request;
        this.authorizationManager = authorizationManager;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String authorization = authorizationManager.getAuthorization(request.getSession().getId());
        if (StringUtils.hasText(authorization)) {
            requestTemplate.header("Authorization", "Bearer " + authorization);
        }

    }
}
