package io.hotcloud.web.controller.rest;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.file.FileUploadService;
import io.hotcloud.web.mvc.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.hotcloud.common.model.WebResponse.created;

@SwaggerBearerAuth
@RestController
@RequestMapping("/v1/files")
@Tag(name = "Files")
@CrossOrigin
public class FilesController {


    private final FileUploadService fileUploadService;

    public FilesController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "Upload minor file to minio server",
            responses = {@ApiResponse(responseCode = "201")},
            parameters = {
                    @Parameter(name = "bucket", description = "bucket name")
            }
    )
    @Log(action = Action.CREATE, target = Target.MINIO, activity = "上传文件")
    public ResponseEntity<Result<String>> upload(@RequestPart("file") MultipartFile file,
                                                 @RequestParam(value = "bucket", required = false) String bucket,
                                                 @RequestParam(value = "public", required = false) Boolean enablePublicPolicy) {
        String upload = fileUploadService.upload(file, bucket, enablePublicPolicy);
        return created(upload);
    }
}
