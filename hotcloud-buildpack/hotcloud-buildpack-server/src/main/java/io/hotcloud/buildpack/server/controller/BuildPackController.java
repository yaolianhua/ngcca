package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.BuildPackPlayer;
import io.hotcloud.buildpack.api.model.BuildPack;
import io.hotcloud.common.Result;
import io.hotcloud.common.WebResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Result<BuildPack>> buildPackResource(
            @RequestParam("git_url") String gitUrl,
            @RequestParam(value = "dockerfile", required = false) String dockerfile,
            @RequestParam(value = "no_push", required = false) Boolean noPush,
            @RequestParam(value = "force", required = false) boolean force,
            @RequestParam(value = "async", required = false) boolean async
    ) {
        BuildPack buildpack = buildPackPlayer.buildpack(gitUrl, dockerfile, force, async, noPush);
        return WebResponse.created(buildpack);
    }
}
