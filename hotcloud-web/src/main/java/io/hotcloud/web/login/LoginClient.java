package io.hotcloud.web.login;

import io.hotcloud.web.feign.ErrorMessageConfiguration;
import io.hotcloud.web.feign.HotCloudServerProperties;
import io.hotcloud.web.mvc.Result;
import io.hotcloud.web.mvc.User;
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
    ResponseEntity<Result<BearerToken>> login(@RequestParam String username,
                                              @RequestParam String password);

    @GetMapping("/v1/security/login")
    ResponseEntity<Result<User>> retrieveUser();

}
