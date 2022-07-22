package io.hotcloud.buildpack.api.core;

import io.hotcloud.common.api.storage.FileHelper;

import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class BuildPackConstant {

    public static final String GIT_PROJECT_ID = "git:project:id";
    public static final String GIT_PROJECT_NAME = "git:project:name";
    public static final String GIT_PROJECT_TARBALL = "git:project:tarball";
    public static final String GIT_PROJECT_IMAGE = "git:project:image";
    public static final String GIT_PROJECT_PATH = "git:project:path";
    public static final String IMAGEBUILD_ARTIFACT = "imagebuild:artifact";

    public static final String DOCKER_SECRET_VOLUME = "docker-registry-secret-volume";
    public static final String WORKSPACE_VOLUME = "workspace-volume";
    public static final String STORAGE_CLASS = "storage-class-buildpack";
    public static final String STORAGE_VOLUME_PATH = Path.of(FileHelper.getUserHome(), "hotcloud", "kaniko").toString();

    public static final String KANIKO_IMAGE = "gcr.io/kaniko-project/executor:latest";

    public static final String K8S_APP = "k8s-app";

    public static final String SUCCESS_MESSAGE = "success";
    public static final String FAILED_MESSAGE = "failed";

    public static final String KANIKO_CONTAINER = "kaniko";
    public static final String KANIKO_INIT_CONTAINER = "init";

    public static final String DOCKER_CONFIG_JSON = ".dockerconfigjson";

    public static final String QUEUE_SUBSCRIBE_BUILDPACK_DONE_MESSAGE = "hotcloud.message.buildpack.done.subscribe";
    public static final String EXCHANGE_FANOUT_BUILDPACK_MESSAGE = "hotcloud.message.buildpack.broadcast";
}
