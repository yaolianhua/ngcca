package io.hotcloud.service.file;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.vendor.minio.MinioProperties;
import io.hotcloud.vendor.minio.service.MinioBucketApi;
import io.hotcloud.vendor.minio.service.MinioObjectApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class FileUploadService {

    private final MinioBucketApi bucketApi;
    private final MinioObjectApi minioObjectApi;
    private final MinioProperties properties;

    public FileUploadService(MinioBucketApi bucketApi,
                             MinioObjectApi minioObjectApi,
                             MinioProperties properties) {
        this.bucketApi = bucketApi;
        this.minioObjectApi = minioObjectApi;
        this.properties = properties;
    }

    public String upload(MultipartFile file, String bucket, boolean enablePublicPolicy) {

        try {
            Assert.notNull(file, "MultipartFile is null");
            if (!StringUtils.hasText(bucket)) {
                bucket = properties.getDefaultBucket();
            }

            if (!bucketApi.exist(bucket)) {
                bucketApi.make(bucket);
            }

            if (Boolean.TRUE.equals(enablePublicPolicy)) {
                bucketApi.setGetObjectPolicy(bucket);
            }

            String filename = file.getOriginalFilename();
            Assert.hasText(filename, "Filename is null");

            long mega = DataSize.ofBytes(file.getSize()).toMegabytes();
            Assert.state(mega < properties.getMaxUploadMegabytes(),
                    "Max upload megabytes is " + properties.getMaxUploadMegabytes() + "MB");

            StopWatch watch = new StopWatch();
            watch.start();
            minioObjectApi.uploadFile(bucket, filename, file.getInputStream(), file.getContentType());

            watch.stop();
            Log.info(this, null,
                    String.format("File [%s] upload success. spend time [%ss]", filename, watch.getTotalTimeSeconds()));
            return String.format("%s/%s/%s", properties.getEndpoint(), bucket, filename);
        } catch (Exception e) {
            throw new PlatformException(e.getMessage());
        }

    }

}
