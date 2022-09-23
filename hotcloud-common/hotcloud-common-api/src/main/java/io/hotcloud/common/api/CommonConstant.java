package io.hotcloud.common.api;

import io.hotcloud.common.api.storage.FileHelper;

import java.nio.file.Path;

public final class CommonConstant {

    public static final String ROOT_PATH = Path.of(FileHelper.getUserHome(), "hotcloud").toString();

    public static final String K8S_APP = "k8s-app";

    public static final String SUCCESS_MESSAGE = "success";

    public static final String TIMEOUT_MESSAGE = "timeout";
    public static final String FAILED_MESSAGE = "failed";
    public static final String CK_IMAGEBUILD_TIMEOUT_SECONDS = "imagebuild:timeout:seconds";
    public static final String CK_IMAGEBUILD_STATUS = "imagebuild:status:%s";
    public static final String CK_DEPLOYMENT_TIMEOUT_SECONDS = "deployment:timeout:seconds";
    public static final String MQ_QUEUE_SECURITY_USER_DELETE = "queue-hotcloud.message.security.user.delete";
    public static final String MQ_EXCHANGE_FANOUT_SECURITY_MODULE = "exchange-hotcloud.message.security.module";
}
