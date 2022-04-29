package io.hotcloud.buildpack.server.controller;

import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackPlayer;
import io.hotcloud.buildpack.server.core.BuildPackCollectionQuery;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.WebResponse.*;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/buildpacks")
public class BuildPackController {

    private final BuildPackPlayer buildPackPlayer;
    private final BuildPackCollectionQuery buildPackCollectionQuery;

    public BuildPackController(BuildPackPlayer buildPackPlayer,
                               BuildPackCollectionQuery buildPackCollectionQuery) {
        this.buildPackPlayer = buildPackPlayer;
        this.buildPackCollectionQuery = buildPackCollectionQuery;
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

    @GetMapping
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
