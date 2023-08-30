package io.hotcloud.server.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.git.gitlab.GitLabRequestParameter;
import io.hotcloud.service.git.gitlab.GitlabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.hotcloud.common.model.WebResponse.ok;

@SwaggerBearerAuth
@RequestMapping("/v1/gitlab")
@RestController
@Tag(name = "Gitlab api")
public class GitlabController {

    private final GitlabService gitlabService;

    public GitlabController(GitlabService gitlabService) {
        this.gitlabService = gitlabService;
    }

    @GetMapping("/projects")
    @Operation(
            summary = "Get gitlab projects",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<List<Project>>> projects(@ParameterObject GitLabRequestParameter parameter) {

        return ok(gitlabService.listProjects(parameter));
    }

    @GetMapping("/ownedprojects")
    @Operation(
            summary = "Get gitlab owned projects",
            responses = {@ApiResponse(responseCode = "200")}
    )
    public ResponseEntity<Result<List<Project>>> ownedprojects(@ParameterObject GitLabRequestParameter parameter) {
        return ok(gitlabService.listOwnedProjects(parameter));
    }

    @GetMapping("/{projectIdOrPath}/branches")
    @Operation(
            summary = "Get gitlab branched",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {@Parameter(name = "projectIdOrPath", description = "project id or path")}
    )
    public ResponseEntity<Result<List<Branch>>> branches(@PathVariable("projectIdOrPath") Object projectIdOrPath,
                                                         @ParameterObject GitLabRequestParameter parameter) {
        return ok(gitlabService.listBranches(projectIdOrPath, parameter));
    }
}
