package io.hotcloud.common.api;

import io.hotcloud.common.api.storage.FileHelper;

import java.nio.file.Path;

public final class CommonConstant {

    public static final String ROOT_PATH = Path.of(FileHelper.getUserHome(), "hotcloud").toString();
}
