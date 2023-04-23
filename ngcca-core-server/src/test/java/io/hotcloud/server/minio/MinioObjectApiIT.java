package io.hotcloud.server.minio;


import io.hotcloud.server.files.FileHelper;

import io.hotcloud.server.NgccaCoreServerApplication;
import io.hotcloud.vendor.minio.MinioObjectApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = NgccaCoreServerApplication.class
)
@ActiveProfiles("test")
@Slf4j
public class MinioObjectApiIT {

    static Path file = Path.of(FileHelper.getUserHome(), "devops-thymeleaf-20220413175715.tar");
    @Autowired
    private MinioObjectApi minioObjectApi;
    @Autowired
    private MinioProperties minioProperties;

    {
        try {
            if (FileHelper.exists(file)) {
                Files.delete(file);
            }
            Files.createFile(file);
            log.info("file '{}' created", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadFileInputStream_then_removed() throws IOException {
        String bucket = minioProperties.getDefaultBucket();
        StopWatch uploadWatch = new StopWatch();
        uploadWatch.start();
        try (InputStream inputStream = Files.newInputStream(file)) {
            minioObjectApi.uploadFile(bucket, "devops-thymeleaf", inputStream, "application/x-tar");
        }
        uploadWatch.stop();
        double seconds = uploadWatch.getTotalTimeSeconds();
        log.info("Upload succeed. Takes '{}s'", seconds);

        String objectUrl = minioObjectApi.getObjectUrl(bucket, "devops-thymeleaf");
        Assertions.assertNotNull(objectUrl);

        StopWatch removeWatch = new StopWatch();
        removeWatch.start();
        minioObjectApi.removed(bucket, "devops-thymeleaf");
        removeWatch.stop();
        double removeWatchTotalTimeSeconds = removeWatch.getTotalTimeSeconds();
        log.info("Removed succeed. Takes '{}s'", removeWatchTotalTimeSeconds);

        boolean exists = Files.deleteIfExists(file);
        log.info("file {} deleted {}", file, exists);
    }

    @Test
    public void upload_then_removed() throws IOException {
        String bucket = minioProperties.getDefaultBucket();
        StopWatch uploadWatch = new StopWatch();
        uploadWatch.start();
        minioObjectApi.uploadFile(bucket, "devops-thymeleaf", file.toFile().getAbsolutePath());
        uploadWatch.stop();
        double seconds = uploadWatch.getTotalTimeSeconds();
        log.info("Upload succeed. Takes '{}s'", seconds);

        StopWatch removeWatch = new StopWatch();
        removeWatch.start();
        minioObjectApi.removed(bucket, "devops-thymeleaf");
        removeWatch.stop();
        double removeWatchTotalTimeSeconds = removeWatch.getTotalTimeSeconds();
        log.info("Removed succeed. Takes '{}s'", removeWatchTotalTimeSeconds);

        boolean exists = Files.deleteIfExists(file);
        log.info("file {} deleted {}", file, exists);
    }
}
