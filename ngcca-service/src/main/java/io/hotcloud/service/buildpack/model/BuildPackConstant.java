package io.hotcloud.service.buildpack.model;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class BuildPackConstant {
    public static final String IMAGEBUILD_ARTIFACT = "imagebuild:artifact";
    public static final String KANIKO_CONTAINER = "kaniko";
    public static final String KANIKO_INIT_GIT_CONTAINER = "git";
    public static final String KANIKO_INIT_ALPINE_CONTAINER = "alpine";

    public static final String DOCKER_CONFIG_JSON = ".dockerconfigjson";

    public static final String QUEUE_SUBSCRIBE_BUILDPACK_DONE_MESSAGE = "hotcloud.message.buildpack.done.subscribe";
    public static final String EXCHANGE_FANOUT_BUILDPACK_MESSAGE = "hotcloud.message.buildpack.broadcast";
}
