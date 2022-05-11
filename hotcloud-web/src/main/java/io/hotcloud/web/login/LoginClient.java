package io.hotcloud.web.login;

import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.user.User;
import io.hotcloud.web.ErrorMessageConfiguration;
import io.hotcloud.web.HotCloudServerProperties;
import io.hotcloud.web.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "loginClient",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallbackFactory = LoginClientFallbackFactory.class,
        configuration = {ErrorMessageConfiguration.class})
public interface LoginClient {

    @PostMapping("/v1/security/login")
    ResponseEntity<R<BearerToken>> login(@RequestParam String username,
                                         @RequestParam String password);

    @GetMapping("/v1/security/login")
    ResponseEntity<R<User>> retrieveUser();

}
