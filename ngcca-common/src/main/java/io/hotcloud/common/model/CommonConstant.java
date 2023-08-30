package io.hotcloud.common.model;

import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.List;

public final class CommonConstant {

    public static final String CONFIG_PREFIX = "ngcca.";
    public static final String ROOT_PATH = Path.of(FileUtils.getUserDirectoryPath(), "ngcca").toString();
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_INIT_PASSWORD = "admin123456";

    public static final String K8S_APP = "k8s-app";
    public static final String K8S_APP_BUSINESS_DATA_ID = "app.business.data/id";

    public static final String SUCCESS_MESSAGE = "success";

    public static final String TIMEOUT_MESSAGE = "timeout";
    public static final String FAILED_MESSAGE = "failed";
    public static final String MESSAGE_QUEUE_TEST = "message:queue:test";
    public static final String MESSAGE_QUEUE_SECURITY = "message:queue:security";
    public static final String MESSAGE_QUEUE_K8S_DEPLOYMENT = "message:queue:k8s:deployment";
    public static final String MESSAGE_QUEUE_K8S_JOB = "message:queue:k8s:job";
    public static final String MESSAGE_QUEUE_K8S_DAEMONSET = "message:queue:k8s:daemonset";
    public static final String MESSAGE_QUEUE_K8S_STATEFULSET = "message:queue:k8s:statefulset";
    public static final String MESSAGE_QUEUE_K8S_CRONJOB = "message:queue:k8s:cronjob";
    public static final String MESSAGE_QUEUE_K8S_POD = "message:queue:k8s:pod";
    public static final String MESSAGE_QUEUE_K8S_AGENT = "message:queue:k8s:agent";
    public static final String MESSAGE_QUEUE_NGCCA = "message:queue:ngcca";
    public static final List<String> MESSAGE_QUEUE_LIST = List.of(
            MESSAGE_QUEUE_TEST,
            MESSAGE_QUEUE_SECURITY,
            MESSAGE_QUEUE_K8S_DEPLOYMENT,
            MESSAGE_QUEUE_K8S_JOB,
            MESSAGE_QUEUE_K8S_CRONJOB,
            MESSAGE_QUEUE_K8S_DAEMONSET,
            MESSAGE_QUEUE_K8S_STATEFULSET,
            MESSAGE_QUEUE_K8S_POD,
            MESSAGE_QUEUE_K8S_AGENT,
            MESSAGE_QUEUE_NGCCA
    );
}
