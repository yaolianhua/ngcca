package io.hotcloud.web.client.user;

import io.hotcloud.security.api.user.User;
import io.hotcloud.web.client.ErrorMessageConfiguration;
import io.hotcloud.web.client.HotCloudServerProperties;
import io.hotcloud.web.client.R;
import io.hotcloud.web.client.RP;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "userClient",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallbackFactory = UserClientFallbackFactory.class, configuration = ErrorMessageConfiguration.class)
public interface UserClient {

    @GetMapping("/v1/security/users/{username}")
    ResponseEntity<R<User>> user(@PathVariable("username") String username);

    @GetMapping("/v1/security/users")
    ResponseEntity<RP<User>> paging(@RequestParam(value = "username", required = false) String username,
                                    @RequestParam(value = "enabled", required = false) Boolean enabled,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "page_size", required = false) Integer pageSize);

    @PostMapping("/v1/security/users")
    ResponseEntity<R<User>> create(@RequestBody User user);
}
