package io.hotcloud.common.storage.minio;

import java.io.InputStream;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface MinioObjectApi {

    /**
     * Get object url
     *
     * @param bucket bucket name
     * @param object object name
     * @return url
     */
    String getObjectUrl(String bucket, String object);

    /**
     * Uploads data from a file to an object
     *
     * @param bucket bucket name
     * @param object object name
     * @param file   file name
     * @return object name
     */
    String uploadFile(String bucket, String object, String file);

    /**
     * Upload file data from {@link InputStream} to an object
     *
     * @param bucket      bucket name
     * @param object      object name
     * @param inputStream file inputStream
     * @return object name
     */
    String uploadFile(String bucket, String object, InputStream inputStream);

    /**
     * Removes an object
     *
     * @param bucket bucket name
     * @param object object name
     */
    void removed(String bucket, String object);
}
