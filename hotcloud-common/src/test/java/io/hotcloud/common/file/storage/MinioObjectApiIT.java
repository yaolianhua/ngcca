package io.hotcloud.common.file.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

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
    public void upload_then_removed() {
        String bucket = minioProperties.getDefaultBucket();
        StopWatch uploadWatch = new StopWatch();
        uploadWatch.start();
        minioObjectApi.upload(bucket, "devops-thymeleaf", "/tmp/kaniko/6f83d4d1c8ad40fdaa4bd9649088a9d8/devops-thymeleaf/devops-thymeleaf-20220413175715.tar");
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
