package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.server.clone.GitClonedCollectionQuery;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.accepted;
import static io.hotcloud.common.WebResponse.okPage;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/git")
public class GitController {

    private final GitClonedService gitClonedService;
    private final GitClonedCollectionQuery gitClonedCollectionQuery;

    public GitController(GitClonedService gitClonedService,
                         GitClonedCollectionQuery gitClonedCollectionQuery) {
        this.gitClonedService = gitClonedService;
        this.gitClonedCollectionQuery = gitClonedCollectionQuery;
    }

    @PostMapping("/clones")
    public ResponseEntity<Result<Void>> cloneRepository(@RequestParam("git_url") String gitUrl,
                                                        @RequestParam(value = "dockerfile", required = false) String dockerfile,
                                                        @RequestParam(value = "branch", required = false) String branch,
                                                        @RequestParam(value = "username", required = false) String username,
                                                        @RequestParam(value = "password", required = false) String password) {
        gitClonedService.clone(gitUrl, dockerfile, branch, username, password);
        return accepted();
    }

    @GetMapping("/clones")
    public ResponseEntity<PageResult<GitCloned>> find(@RequestParam(value = "user", required = false) String user,
                                                      @RequestParam(value = "success", required = false) Boolean success,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<GitCloned> pageResult = gitClonedCollectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
