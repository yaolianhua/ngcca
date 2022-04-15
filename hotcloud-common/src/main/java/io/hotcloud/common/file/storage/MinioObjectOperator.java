package io.hotcloud.common.file.storage;

import io.hotcloud.common.exception.HotCloudException;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class MinioObjectOperator implements MinioObjectApi {

    private final MinioClient minioClient;

    public MinioObjectOperator(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void upload(String bucket, String object, String file) {
        try {
            UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .filename(file)
                    .build();
            minioClient.uploadObject(objectArgs);
        } catch (Exception ex) {
            throw new HotCloudException("upload failed: " + ex.getMessage() + "");
        }

    }

    @Override
    public void removed(String bucket, String object) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception ex) {
            throw new HotCloudException("remove failed: " + ex.getMessage() + "");
        }

    }
}
