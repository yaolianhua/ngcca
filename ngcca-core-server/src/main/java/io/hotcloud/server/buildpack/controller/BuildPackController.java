package io.hotcloud.server.buildpack.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.module.buildpack.BuildPack;
import io.hotcloud.module.buildpack.BuildPackPlayer;
import io.hotcloud.server.buildpack.service.BuildPackCollectionQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.model.WebResponse.*;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/buildpacks")
@Tag(name = "BuildPack")
@Deprecated(since = "BuildPackApiV2")
public class BuildPackController {

    private final BuildPackPlayer buildPackPlayer;
    private final BuildPackCollectionQuery buildPackCollectionQuery;

    public BuildPackController(BuildPackPlayer buildPackPlayer,
                               BuildPackCollectionQuery buildPackCollectionQuery) {
        this.buildPackPlayer = buildPackPlayer;
        this.buildPackCollectionQuery = buildPackCollectionQuery;
    }

    @PostMapping
    @Operation(
            summary = "Deploy a buildPack job",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "cloned_id", description = "Git cloned repository id", required = true),
                    @Parameter(name = "no_push", description = "whether pushed the build image to remote registry", schema = @Schema(allowableValues = {"true", "false"}))
            }
    )
    public ResponseEntity<Result<BuildPack>> apply(
            @RequestParam("cloned_id") String clonedId,
            @RequestParam(value = "no_push", required = false) Boolean noPush
    ) {
        BuildPack buildpack = buildPackPlayer.apply(clonedId, noPush);
        return created(buildpack);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete buildPack ",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "BuildPack id"),
                    @Parameter(name = "physically", description = "whether delete physically", schema = @Schema(allowableValues = {"true", "false"}))
            }
    )
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id,
                                               @RequestParam("physically") boolean physically) {
        buildPackPlayer.delete(id, physically);
        return accepted();
    }

    @GetMapping
    @Operation(
            summary = "BuildPack paging query",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "user", description = "user queried"),
                    @Parameter(name = "cloned_id", description = "Git cloned repository id"),
                    @Parameter(name = "done", description = "whether the buildPack has done", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "deleted", description = "whether the buildPack has been deleted", schema = @Schema(allowableValues = {"true", "false"})),
                    @Parameter(name = "page", description = "current page", schema = @Schema(defaultValue = "1")),
                    @Parameter(name = "page_size", description = "pageSize", schema = @Schema(defaultValue = "10"))
            }
    )
    public ResponseEntity<PageResult<BuildPack>> find(@RequestParam(value = "user", required = false) String user,
                                                      @RequestParam(value = "cloned_id", required = false) String clonedId,
                                                      @RequestParam(value = "done", required = false) Boolean done,
                                                      @RequestParam(value = "deleted", required = false) Boolean deleted,
                                                      @RequestParam(value = "page", required = false) Integer page,
                                                      @RequestParam(value = "page_size", required = false) Integer pageSize) {

        PageResult<BuildPack> pageResult = buildPackCollectionQuery.pagingQuery(user, clonedId, done, deleted, Pageable.of(page, pageSize));
        return okPage(pageResult);
    }

}
