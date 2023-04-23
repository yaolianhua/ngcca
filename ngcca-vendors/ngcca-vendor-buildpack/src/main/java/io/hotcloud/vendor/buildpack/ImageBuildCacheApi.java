package io.hotcloud.vendor.buildpack;

public interface ImageBuildCacheApi {

    void setStatus(String buildPackId, ImageBuildStatus status);

    ImageBuildStatus getStatus(String buildPackId);

    boolean tryLock(String buildPackId);

    void unLock(String buildPackId);

    Integer getTimeoutSeconds();
}
