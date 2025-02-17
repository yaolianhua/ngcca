package io.hotcloud.vendor.minio.service;

import io.hotcloud.common.model.exception.PlatformException;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.Map;

@Component
class MinioObjectOperator implements MinioObjectApi {

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
            throw new PlatformException("get object url failed: " + ex.getMessage());
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
            throw new PlatformException("upload failed: " + ex.getMessage());
        }

    }

    @Override
    public String uploadFile(String bucket, String object, InputStream inputStream, String contentType) {
        if (!StringUtils.hasText(contentType)) {
            contentType = "application/octet-stream";
        }
        Map<String, String> headers = Map.of("Content-Type", contentType);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .headers(headers)
                    .bucket(bucket)
                    .object(object)
                    .build();
            minioClient.putObject(putObjectArgs);
            return object;
        } catch (Exception ex) {
            throw new PlatformException("upload failed: " + ex.getMessage());
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
            throw new PlatformException("remove failed: " + ex.getMessage());
        }

    }
}
