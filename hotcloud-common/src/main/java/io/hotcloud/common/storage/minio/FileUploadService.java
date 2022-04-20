package io.hotcloud.common.storage.minio;

import io.hotcloud.common.UUIDGenerator;
import io.hotcloud.common.exception.HotCloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

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

    public String upload(MultipartFile file, String bucket) {
        Assert.notNull(file, "MultipartFile is null");
        if (!StringUtils.hasText(bucket)) {
            bucket = properties.getDefaultBucket();
        }

        if (!bucketApi.exist(bucket)) {
            bucketApi.make(bucket);
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.hasText(filename)) {
            filename = UUIDGenerator.uuidNoDash();
        }
        try {
            minioObjectApi.uploadFile(bucket, filename, file.getInputStream());
            return Path.of(properties.getEndpoint(), bucket, filename).toString();
        } catch (Exception e) {
            throw new HotCloudException(e.getMessage());
        }

    }
}
