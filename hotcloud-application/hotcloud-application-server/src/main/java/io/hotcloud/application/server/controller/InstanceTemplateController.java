package io.hotcloud.application.server.controller;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplatePlayer;
import io.hotcloud.application.api.template.Template;
import io.hotcloud.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.accepted;
import static io.hotcloud.common.WebResponse.created;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/application/instances")
public class InstanceTemplateController {

    private final InstanceTemplatePlayer instanceTemplatePlayer;

    public InstanceTemplateController(InstanceTemplatePlayer instanceTemplatePlayer) {
        this.instanceTemplatePlayer = instanceTemplatePlayer;
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
}
