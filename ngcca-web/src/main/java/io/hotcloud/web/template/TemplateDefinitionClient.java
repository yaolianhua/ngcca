package io.hotcloud.web.template;

import io.hotcloud.web.feign.ErrorMessageConfiguration;
import io.hotcloud.web.feign.HotCloudServerProperties;
import io.hotcloud.web.mvc.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@FeignClient(name = "templateDefinitionClient",
        path = "/v1/definition/templates",
        url = HotCloudServerProperties.HOTCLOUD_SERVER,
        fallbackFactory = TemplateDefinitionClientFallbackFactory.class,
        configuration = {ErrorMessageConfiguration.class})
public interface TemplateDefinitionClient {

    @PostMapping
    ResponseEntity<Result<TemplateDefinition>> create(@RequestBody TemplateDefinition definition);

    @PutMapping
    ResponseEntity<Result<TemplateDefinition>> update(@RequestBody TemplateDefinition definition);

    @GetMapping("/{id}")
    ResponseEntity<Result<TemplateDefinition>> findOne(@PathVariable("id") String id);

    @GetMapping
    ResponseEntity<Result<List<TemplateDefinition>>> findAll(@RequestParam(value = "name", required = false) String name);

    @GetMapping("/classification")
    ResponseEntity<Result<List<String>>> classification();

    @DeleteMapping("/{id}")
    ResponseEntity<Result<Void>> delete(@PathVariable("id") String id);
}
