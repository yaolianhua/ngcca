package io.hotcloud.buildpack.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class BuildPackConstant {

    public static final String GIT_PROJECT_NAME = "git:project:name";
    public static final String GIT_PROJECT_TARBALL = "git:project:tarball";
    public static final String GIT_PROJECT_PATH = "git:project:path";

    public static final String DOCKER_SECRET_VOLUME = "docker-registry-secret-volume";
    public static final String WORKSPACE_VOLUME = "workspace-volume";
    public static final String STORAGE_CLASS = "storage-class-buildpack";
    public static final String STORAGE_VOLUME_PATH = "/tmp/kaniko";

    public static final String KANIKO_IMAGE = "gcr.io/kaniko-project/executor:latest";

    public static final String K8S_APP = "k8s-app";

    public static final String KANIKO_CONTAINER = "kaniko";

    public static final String DOCKER_CONFIG_JSON = ".dockerconfigjson";
}
