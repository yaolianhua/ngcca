package io.hotcloud.common.server.controller;

import io.hotcloud.common.api.Result;
import io.hotcloud.common.server.core.minio.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.hotcloud.common.api.WebResponse.created;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/storage")
@Tag(name = "Storage")
public class StorageController {


    private final FileUploadService fileUploadService;

    public StorageController(FileUploadService fileUploadService) {
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
