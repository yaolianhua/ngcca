package io.hotcloud.common.storage.minio;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface MinioBucketApi {

    /**
     * Create bucket
     *
     * @param bucket bucket name
     */
    void make(String bucket);

    /**
     * Remove bucket
     *
     * @param bucket bucket name
     */
    void remove(String bucket);

    /**
     * Whether the bucket exists
     *
     * @param bucket bucket name
     * @return true/false
     */
    boolean exist(String bucket);
}
