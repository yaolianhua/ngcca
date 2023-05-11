package io.hotcloud.server.module.application.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.server.module.application.core.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.hotcloud.common.model.WebResponse.created;

@RestController
@RequestMapping("/v1/files")
@Tag(name = "Files")
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
    public ResponseEntity<Result<String>> upload(@RequestPart("file") MultipartFile file,
                                                 @RequestParam(value = "bucket", required = false) String bucket) {
        String upload = fileUploadService.upload(file, bucket);
        return created(upload);
    }
}
