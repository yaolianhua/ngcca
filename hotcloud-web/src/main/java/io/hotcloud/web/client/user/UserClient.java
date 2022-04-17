package io.hotcloud.web.client.user;

import io.hotcloud.security.user.model.User;
import io.hotcloud.web.client.HotCloudServerProperties;
import io.hotcloud.web.client.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "userClient",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/v1/security/users")
    ResponseEntity<R<User>> user(@RequestParam("username") String username);

}
