package io.hotcloud.web.statistics;

import io.hotcloud.web.feign.ErrorMessageConfiguration;
import io.hotcloud.web.feign.HotCloudServerProperties;
import io.hotcloud.web.mvc.PageResult;
import io.hotcloud.web.mvc.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "statisticsClient",
        path = "/v1/statistics",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallbackFactory = StatisticsClientFallbackFactory.class,
        configuration = {ErrorMessageConfiguration.class})
public interface StatisticsClient {

    @GetMapping("/{userid}")
    ResponseEntity<Result<Statistics>> statistics(@PathVariable("userid") String userid);

    @GetMapping
    ResponseEntity<PageResult<Statistics>> statistics(@RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "page_size", required = false) Integer pageSize);
}
