package io.hotcloud.common.server.storage.minio;

import io.hotcloud.common.api.storage.minio.MinioObjectApi;
import io.hotcloud.common.api.storage.minio.MinioProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class MinioObjectApiIT extends MinioIT {

    @Autowired
    private MinioObjectApi minioObjectApi;
    @Autowired
    private MinioProperties minioProperties;

    @Test
    public void uploadFileInputStream_then_removed() throws IOException {
        String bucket = minioProperties.getDefaultBucket();
        StopWatch uploadWatch = new StopWatch();
        uploadWatch.start();
        Path filePath = Path.of("/tmp/kaniko/6f83d4d1c8ad40fdaa4bd9649088a9d8/devops-thymeleaf/devops-thymeleaf-20220413175715.tar");
        try (InputStream inputStream = Files.newInputStream(filePath)) {
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
    }
    @Test
    public void upload_then_removed() {
        String bucket = minioProperties.getDefaultBucket();
        StopWatch uploadWatch = new StopWatch();
        uploadWatch.start();
        minioObjectApi.uploadFile(bucket, "devops-thymeleaf", "/tmp/kaniko/6f83d4d1c8ad40fdaa4bd9649088a9d8/devops-thymeleaf/devops-thymeleaf-20220413175715.tar");
        uploadWatch.stop();
        double seconds = uploadWatch.getTotalTimeSeconds();
        log.info("Upload succeed. Takes '{}s'", seconds);

        StopWatch removeWatch = new StopWatch();
        removeWatch.start();
        minioObjectApi.removed(bucket, "devops-thymeleaf");
        removeWatch.stop();
        double removeWatchTotalTimeSeconds = removeWatch.getTotalTimeSeconds();
        log.info("Removed succeed. Takes '{}s'", removeWatchTotalTimeSeconds);
    }
}
