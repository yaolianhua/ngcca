package io.hotcloud.application.server.controller;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.application.server.template.InstanceTemplateCollectionQuery;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/application/instances")
public class InstanceTemplateController {

    private final InstanceTemplatePlayer instanceTemplatePlayer;
    private final InstanceTemplateCollectionQuery collectionQuery;

    public InstanceTemplateController(InstanceTemplatePlayer instanceTemplatePlayer,
                                      InstanceTemplateCollectionQuery collectionQuery) {
        this.instanceTemplatePlayer = instanceTemplatePlayer;
        this.collectionQuery = collectionQuery;
    }

    @PostMapping
    public ResponseEntity<Result<InstanceTemplate>> apply(Template template) {
        InstanceTemplate instanceTemplate = instanceTemplatePlayer.play(template);
        return created(instanceTemplate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        instanceTemplatePlayer.delete(id);
        return accepted();
    }

    @GetMapping
    public ResponseEntity<PageResult<InstanceTemplate>> page(@RequestParam(value = "user", required = false) String user,
                                                             @RequestParam(value = "success", required = false) Boolean success,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<InstanceTemplate> pageResult = collectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
