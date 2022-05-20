package io.hotcloud.web.activity;

import io.hotcloud.web.feign.ErrorMessageConfiguration;
import io.hotcloud.web.feign.HotCloudServerProperties;
import io.hotcloud.web.mvc.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "activityClient",
        path = "/v1/activities",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallbackFactory = ActivityClientFallbackFactory.class,
        configuration = {ErrorMessageConfiguration.class})
public interface ActivityClient {

    @GetMapping
    ResponseEntity<PageResult<Activity>> activities(@RequestParam(value = "user") String user,
                                                    @RequestParam(value = "target", required = false) String target,
                                                    @RequestParam(value = "action", required = false) String action,
                                                    @RequestParam(value = "page", required = false) Integer page,
                                                    @RequestParam(value = "page_size", required = false) Integer pageSize);
}
