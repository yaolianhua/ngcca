package io.hotcloud.server.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.volume.VolumeCreateService;
import io.hotcloud.service.volume.VolumeDeleteService;
import io.hotcloud.service.volume.VolumeQueryService;
import io.hotcloud.service.volume.Volumes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.hotcloud.common.model.WebResponse.*;


@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/volumes")
@Tag(name = "volumes api")
public class VolumesController {

    private final VolumeCreateService volumeCreateService;
    private final VolumeQueryService volumeQueryService;
    private final VolumeDeleteService volumeDeleteService;

    public VolumesController(VolumeCreateService volumeCreateService,
                             VolumeQueryService volumeQueryService,
                             VolumeDeleteService volumeDeleteService) {
        this.volumeCreateService = volumeCreateService;
        this.volumeQueryService = volumeQueryService;
        this.volumeDeleteService = volumeDeleteService;
    }

    @PostMapping
    @Operation(
            summary = "create volume",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "name", description = "volume name"),
                    @Parameter(name = "gigabytes", description = "volume capacity")
            }
    )
    public ResponseEntity<Result<Volumes>> create(@RequestParam("name") String name,
                                                  @RequestParam("gigabytes") Integer gigabytes) {
        Volumes volumes = volumeCreateService.create(name, gigabytes);
        return created(volumes);
    }

    @GetMapping
    @Operation(
            summary = "page query volume list",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "page", description = "page num"),
                    @Parameter(name = "pageSize", description = "page size")
            }
    )
    public ResponseEntity<PageResult<Volumes>> pageQuery(@ParameterObject Pageable pageable) {
        PageResult<Volumes> pageResult = volumeQueryService.pageQuery(pageable);
        return okPage(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "query volume",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {
                    @Parameter(name = "id", description = "volume id")
            }
    )
    public ResponseEntity<Result<Volumes>> query(@PathVariable("id") String id) {
        Volumes volumes = volumeQueryService.queryOne(id);
        return ok(volumes);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "delete volume",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "id", description = "volume id")
            }
    )
    public ResponseEntity<Result<Void>> delete(@PathVariable("id") String id) {
        volumeDeleteService.delete(id);
        return none();
    }

    @DeleteMapping("/users/{username}")
    @Operation(
            summary = "delete user volume",
            responses = {@ApiResponse(responseCode = "202")},
            parameters = {
                    @Parameter(name = "username", description = "username")
            }
    )
    public ResponseEntity<Result<Void>> deleteByUsername(@PathVariable("username") String username) {
        volumeDeleteService.deleteByUsername(username);
        return none();
    }

    @DeleteMapping
    @Operation(
            summary = "delete user volume",
            responses = {@ApiResponse(responseCode = "202")}
    )
    public ResponseEntity<Result<Void>> deleteAll() {
        volumeDeleteService.delete();
        return none();
    }

}
