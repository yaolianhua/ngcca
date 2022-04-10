package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.api.core.model.BuildPack;
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
    public ResponseEntity<Result<BuildPack>> apply(
            @RequestParam("git_url") String url,
            @RequestParam(value = "dockerfile", required = false) String dockerfile,
            @RequestParam(value = "no_push", required = false) Boolean noPush
    ) {
        BuildPack buildpack = buildPackPlayer.apply(url, dockerfile, noPush);
        return WebResponse.created(buildpack);
    }

}
