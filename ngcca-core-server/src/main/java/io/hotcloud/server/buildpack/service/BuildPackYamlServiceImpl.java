package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.exception.NGCCACommonException;
import io.hotcloud.vendor.buildpack.BuildPackYamlService;
import io.hotcloud.vendor.buildpack.kaniko.DockerfileTemplateRender;
import io.hotcloud.vendor.buildpack.kaniko.KanikoJobTemplateRender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BuildPackYamlServiceImpl implements BuildPackYamlService {

    @Override
    public String search(String type) {
        if (!StringUtils.hasText(type)) {
            return "";
        }

        if ("dockerfile-jar".equalsIgnoreCase(type)) {
            return DockerfileTemplateRender.DOCKERFILE_JAR_TEMPLATE;
        }
        if ("dockerfile-jar-maven".equalsIgnoreCase(type)) {
            return DockerfileTemplateRender.DOCKERFILE_JAR_MAVEN_TEMPLATE;
        }
        if ("dockerfile-war".equalsIgnoreCase(type)) {
            return DockerfileTemplateRender.DOCKERFILE_WAR_TEMPLATE;
        }

        if ("imagebuild-source".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.IMAGEBUILD_SOURCE_TEMPLATE;
        }
        if ("imagebuild-jar-war".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.IMAGEBUILD_JAR_WAR_TEMPLATE;
        }
        if ("imagebuild-secret".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.IMAGEBUILD_SECRET_TEMPLATE;
        }
        throw new NGCCACommonException("Unsupported type [" + type + "]", 400);
    }
}
