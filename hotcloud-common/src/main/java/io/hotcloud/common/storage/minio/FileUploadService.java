package io.hotcloud.common.storage.minio;

import io.hotcloud.common.exception.HotCloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class FileUploadService {

    private final MinioBucketApi bucketApi;
    private final MinioObjectApi minioObjectApi;

    public FileUploadService(MinioBucketApi bucketApi,
                             MinioObjectApi minioObjectApi) {
        this.bucketApi = bucketApi;
        this.minioObjectApi = minioObjectApi;
    }

    public String upload(MultipartFile file, String bucket) {
        Assert.notNull(file, "MultipartFile is null");
        Assert.hasText(bucket, "Bucket name is null");

        if (!bucketApi.exist(bucket)) {
            bucketApi.make(bucket);
        }

        String filename = file.getOriginalFilename();
        try {
            return minioObjectApi.uploadFile(bucket, filename, file.getInputStream());
        } catch (Exception e) {
            throw new HotCloudException(e.getMessage());
        }

    }
}
