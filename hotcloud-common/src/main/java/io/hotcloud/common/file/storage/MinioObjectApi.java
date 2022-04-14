package io.hotcloud.common.file.storage;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface MinioObjectApi {

    /**
     * Uploads data from a file to an object
     *
     * @param bucket bucket name
     * @param object object name
     * @param file   file name
     */
    void upload(String bucket, String object, String file);

    /**
     * Removes an object
     *
     * @param bucket bucket name
     * @param object object name
     */
    void removed(String bucket, String object);
}
