package io.hotcloud.common.api;

import io.hotcloud.common.api.storage.FileHelper;

import java.nio.file.Path;

public final class CommonConstant {

    public static final String ROOT_PATH = Path.of(FileHelper.getUserHome(), "hotcloud").toString();

    public static final String K8S_APP = "k8s-app";

    public static final String SUCCESS_MESSAGE = "success";

    public static final String TIMEOUT_MESSAGE = "timeout";
    public static final String FAILED_MESSAGE = "failed";
}
