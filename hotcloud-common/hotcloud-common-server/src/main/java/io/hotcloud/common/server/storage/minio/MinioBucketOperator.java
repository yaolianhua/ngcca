package io.hotcloud.common.server.storage.minio;

import io.hotcloud.common.api.exception.HotCloudException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveBucketArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class MinioBucketOperator implements MinioBucketApi {

    private final MinioClient minioClient;

    public MinioBucketOperator(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void make(String bucket) {
        try {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build();
            minioClient.makeBucket(makeBucketArgs);
        } catch (Exception ex) {
            throw new HotCloudException("make bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public void remove(String bucket) {
        try {
            RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder()
                    .bucket(bucket)
                    .build();
            minioClient.removeBucket(removeBucketArgs);
        } catch (Exception ex) {
            throw new HotCloudException("remove bucket failed: " + ex.getMessage());
        }
    }

    @Override
    public boolean exist(String bucket) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build();
            return minioClient.bucketExists(bucketExistsArgs);
        } catch (Exception ex) {
            throw new HotCloudException("exist bucket failed: " + ex.getMessage());
        }
    }
}
