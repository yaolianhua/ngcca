package io.hotcloud.common.model;

import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.List;

public final class CommonConstant {

    public static final String CONFIG_PREFIX = "ngcca.";
    public static final String ROOT_PATH = Path.of(FileUtils.getUserDirectoryPath(), "ngcca").toString();

    public static final String K8S_APP = "k8s-app";
    public static final String K8S_APP_BUSINESS_DATA_ID = "app.business.data/id";

    public static final String SUCCESS_MESSAGE = "success";

    public static final String TIMEOUT_MESSAGE = "timeout";
    public static final String FAILED_MESSAGE = "failed";
    public static final String MESSAGE_QUEUE_TEST = "message:queue:test";
    public static final String MESSAGE_QUEUE_SECURITY = "message:queue:security";
    public static final String MESSAGE_QUEUE_TEMPLATE_DEPLOYMENT = "message:queue:template:deployment";
    public static final String MESSAGE_QUEUE_BUILD_JOB = "message:queue:build:job";
    public static final String MESSAGE_QUEUE_APPLICATION_DEPLOYMENT = "message:queue:application:deployment";
    public static final String MESSAGE_QUEUE_K8S_AGENT = "message:queue:k8s:agent";
    public static final List<String> MESSAGE_QUEUE_LIST = List.of(
            CommonConstant.MESSAGE_QUEUE_TEST,
            CommonConstant.MESSAGE_QUEUE_SECURITY,
            CommonConstant.MESSAGE_QUEUE_TEMPLATE_DEPLOYMENT,
            CommonConstant.MESSAGE_QUEUE_BUILD_JOB,
            CommonConstant.MESSAGE_QUEUE_APPLICATION_DEPLOYMENT,
            CommonConstant.MESSAGE_QUEUE_K8S_AGENT
    );
}
