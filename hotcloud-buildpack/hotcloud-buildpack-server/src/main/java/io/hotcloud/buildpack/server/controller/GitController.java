package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.clone.GitClonedService;
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

    private final GitClonedService gitClonedService;

    public GitController(GitClonedService gitClonedService) {
        this.gitClonedService = gitClonedService;
    }

    @PostMapping("/clone")
    public ResponseEntity<Result<Void>> cloneRepository(@RequestParam("git_url") String gitUrl,
                                                        @RequestParam(value = "branch", required = false) String branch,
                                                        @RequestParam(value = "username", required = false) String username,
                                                        @RequestParam(value = "password", required = false) String password) {
        gitClonedService.clone(gitUrl, branch, username, password);
        return WebResponse.accepted();
    }
}
