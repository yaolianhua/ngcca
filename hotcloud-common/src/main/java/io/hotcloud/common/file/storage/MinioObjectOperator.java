package io.hotcloud.common.file.storage;

import io.hotcloud.common.exception.HotCloudException;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

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
    public String getObjectUrl(String bucket, String object) {
        try {
            //expiry must be minimum 1 second to maximum 7 days
            GetPresignedObjectUrlArgs presignedObjectUrlArgs = GetPresignedObjectUrlArgs
                    .builder()
                    .bucket(bucket)
                    .object(object)
                    .method(Method.GET)
                    .build();
            return minioClient.getPresignedObjectUrl(presignedObjectUrlArgs);
        } catch (Exception ex) {
            throw new HotCloudException("get object url failed: " + ex.getMessage());
        }
    }

    @Override
    public String uploadFile(String bucket, String object, String file) {
        try {
            UploadObjectArgs objectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(object)
                    .filename(file)
                    .build();
            minioClient.uploadObject(objectArgs);
            return object;
        } catch (Exception ex) {
            throw new HotCloudException("upload failed: " + ex.getMessage());
        }

    }

    @Override
    public String uploadFile(String bucket, String object, InputStream inputStream) {
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucket)
                    .object(object)
                    .build();
            minioClient.putObject(putObjectArgs);
            return object;
        } catch (Exception ex) {
            throw new HotCloudException("upload failed: " + ex.getMessage());
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
            throw new HotCloudException("remove failed: " + ex.getMessage());
        }

    }
}
