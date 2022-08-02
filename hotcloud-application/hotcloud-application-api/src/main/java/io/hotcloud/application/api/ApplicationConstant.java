package io.hotcloud.application.api;

import io.hotcloud.common.api.storage.FileHelper;

import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class ApplicationConstant {
    public static final String STORAGE_VOLUME_PATH = Path.of(FileHelper.getUserHome(), "hotcloud", "app").toString();

    public static final String K8S_APP = "k8s-app";


}
