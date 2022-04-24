package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.accepted;
import static io.hotcloud.common.WebResponse.created;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/buildpack")
public class BuildPackController {

    private final BuildPackPlayer buildPackPlayer;

    public BuildPackController(BuildPackPlayer buildPackPlayer) {
        this.buildPackPlayer = buildPackPlayer;
    }

    @PostMapping
    public ResponseEntity<Result<BuildPack>> apply(
            @RequestParam("cloned_id") String clonedId,
            @RequestParam(value = "no_push", required = false) Boolean noPush
    ) {
        BuildPack buildpack = buildPackPlayer.apply(clonedId, noPush);
        return created(buildpack);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        buildPackPlayer.delete(id);
        return accepted();
    }

}
