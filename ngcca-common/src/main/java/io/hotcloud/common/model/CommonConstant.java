package io.hotcloud.common.model;

public final class CommonConstant {

    public static final String CONFIG_PREFIX = "ngcca.";
    public static final String ROOT_PATH = "/ngcca";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_INIT_PASSWORD = "admin123456";
    public static final String DEFAULT_CLUSTER_ID = "default-cluster";
    public static final String DEFAULT_CLUSTER_NAME = "default-cluster";

    @Deprecated(forRemoval = true)
    public static final String K8S_APP = K8sLabel.K8S_APP;
    @Deprecated(forRemoval = true)
    public static final String K8S_APP_BUSINESS_DATA_ID = K8sLabel.K8S_APP_BUSINESS_DATA_ID;

    public static final String SUCCESS_MESSAGE = "success";
    public static final String APPLICATION_DEPLOYING_MESSAGE = "deploying";
    public static final String APPLICATION_BUILD_FAILED_MESSAGE = "build failed";

    public static final String TIMEOUT_MESSAGE = "timeout";
    public static final String FAILED_MESSAGE = "failed";
}
