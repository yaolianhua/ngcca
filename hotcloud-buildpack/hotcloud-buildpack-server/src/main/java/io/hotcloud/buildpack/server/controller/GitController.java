package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.server.clone.GitClonedCollectionQuery;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.created;
import static io.hotcloud.common.WebResponse.okPage;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/git")
@Tag(name = "Git clone")
public class GitController {

    private final GitClonedService gitClonedService;
    private final GitClonedCollectionQuery gitClonedCollectionQuery;

    public GitController(GitClonedService gitClonedService,
                         GitClonedCollectionQuery gitClonedCollectionQuery) {
        this.gitClonedService = gitClonedService;
        this.gitClonedCollectionQuery = gitClonedCollectionQuery;
    }

    @PostMapping("/clones")
    @Operation(
            summary = "Clone repository",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "git_url", description = "repository git url", required = true),
                    @Parameter(name = "dockerfile", description = "dockerfile name in git repository", schema = @Schema(defaultValue = "Dockerfile")),
                    @Parameter(name = "branch", description = "which branch will be cloned", schema = @Schema(defaultValue = "master")),
                    @Parameter(name = "username", description = "username credential for private git repository"),
                    @Parameter(name = "password", description = "password credential for private git repository")
            }
    )
    public ResponseEntity<Result<Void>> cloneRepository(@RequestParam("git_url") String gitUrl,
                                                        @RequestParam(value = "dockerfile", required = false) String dockerfile,
                                                        @RequestParam(value = "branch", required = false) String branch,
                                                        @RequestParam(value = "username", required = false) String username,
                                                        @RequestParam(value = "password", required = false) String password) {
        gitClonedService.clone(gitUrl, dockerfile, branch, username, password);
        return created();
    }

    @GetMapping("/clones")
    @Operation(
            summary = "Git cloned repository paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user queried"),
                    @Parameter(name = "success", description = "whether the cloned repository successful", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    public ResponseEntity<PageResult<GitCloned>> find(@RequestParam(value = "user", required = false) String user,
                                                      @RequestParam(value = "success", required = false) Boolean success,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "page_size", required = false) Integer pageSize) {
        PageResult<GitCloned> pageResult = gitClonedCollectionQuery.pagingQuery(user, success, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }
}
