package io.hotcloud.service.module.buildpack.service;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.module.buildpack.BuildPackYamlService;
import io.hotcloud.vendor.kaniko.DockerfileTemplateRender;
import io.hotcloud.vendor.kaniko.KanikoJobTemplateRender;
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
            return DockerfileTemplateRender.JAR_TEMPLATE_DOCKERFILE;
        }
        if ("dockerfile-jar-maven".equalsIgnoreCase(type)) {
            return DockerfileTemplateRender.MAVEN_JAR_TEMPLATE_DOCKERFILE;
        }
        if ("dockerfile-war".equalsIgnoreCase(type)) {
            return DockerfileTemplateRender.WAR_TEMPLATE_DOCKERFILE;
        }

        if ("imagebuild-source".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.SOURCE_CODE_TEMPLATE_YAML;
        }
        if ("imagebuild-jar-war".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.ARTIFACT_TEMPLATE_YAML;
        }
        if ("imagebuild-secret".equalsIgnoreCase(type)) {
            return KanikoJobTemplateRender.SECRET_TEMPLATE_YAML;
        }
        throw new PlatformException("Unsupported type [" + type + "]", 400);
    }
}
