package io.hotcloud.web.user;

import io.hotcloud.web.feign.ErrorMessageConfiguration;
import io.hotcloud.web.feign.HotCloudServerProperties;
import io.hotcloud.web.mvc.PageResult;
import io.hotcloud.web.mvc.Result;
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

    @GetMapping("/v1/security/users/{username}/user")
    ResponseEntity<Result<User>> findUserByUsername(@PathVariable("username") String username);

    @GetMapping("/v1/security/users")
    ResponseEntity<PageResult<User>> paging(@RequestParam(value = "username", required = false) String username,
                                            @RequestParam(value = "enabled", required = false) Boolean enabled,
                                            @RequestParam(value = "page", required = false) Integer page,
                                            @RequestParam(value = "page_size", required = false) Integer pageSize);

    @GetMapping("/v1/security/users/{id}")
    ResponseEntity<Result<User>> findUserById(@PathVariable String id);

    @PostMapping("/v1/security/users")
    ResponseEntity<Result<User>> create(@RequestBody User user);

    @DeleteMapping("/v1/security/users/{id}")
    ResponseEntity<Result<Void>> delete(@PathVariable String id);

    @PutMapping("/v1/security/users")
    ResponseEntity<Result<User>> update(@RequestBody User user);

    @PutMapping("/v1/security/users/{username}/{enable}")
    ResponseEntity<Result<Void>> onOff(@PathVariable String username,
                                       @PathVariable Boolean enable);
}
