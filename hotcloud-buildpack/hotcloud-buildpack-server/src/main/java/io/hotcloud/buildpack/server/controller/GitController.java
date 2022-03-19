package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.GitApi;
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
@RequestMapping("/v1/git")
public class GitController {

    private final GitApi gitApi;

    public GitController(GitApi gitApi) {
        this.gitApi = gitApi;
    }

    @PostMapping("/clone")
    public ResponseEntity<Result<Boolean>> cloneRepository(@RequestParam("gitUrl") String gitUrl,
                                                           @RequestParam(value = "branch", required = false) String branch,
                                                           @RequestParam("localPath") String local,
                                                           @RequestParam(value = "username", required = false) String username,
                                                           @RequestParam(value = "password", required = false) String password) {
        Boolean clone = gitApi.clone(gitUrl, branch, local, username, password);
        return WebResponse.ok(clone);
    }
}
