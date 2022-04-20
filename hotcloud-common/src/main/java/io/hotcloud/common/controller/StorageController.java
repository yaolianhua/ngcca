package io.hotcloud.common.controller;

import io.hotcloud.common.Result;
import io.hotcloud.common.storage.minio.FileUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.hotcloud.common.WebResponse.created;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/storage")
public class StorageController {


    private final FileUploadService fileUploadService;

    public StorageController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Result<String>> upload(@RequestPart MultipartFile file,
                                                 @RequestParam(value = "bucket", required = false) String bucket) {
        String upload = fileUploadService.upload(file, bucket);
        return created(upload);
    }
}
