package io.hotcloud.common.api.core.minio;

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

    /**
     * Set {@code GetObject} bucket policy
     * <p> e.g. <a href="https://minio-api.docker.local/bucket/your-file">download your-file</a>
     * @param bucket bucket name
     */
    void setGetObjectPolicy(String bucket);
}
