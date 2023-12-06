package io.hotcloud.service.template.model;

import io.hotcloud.common.file.FileHelper;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.service.template.Template;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class JenkinsTemplate {

    public static final String TEMPLATE;

    static {
        try {
            TEMPLATE = new BufferedReader(new InputStreamReader(new ClassPathResource("jenkins.yaml").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String name = Template.JENKINS.name().toLowerCase();
    private String image = "jenkins/jenkins:lts";
    private String namespace;
    private String service = Template.JENKINS.name().toLowerCase();

    public JenkinsTemplate(String namespace) {
        this.namespace = namespace;
    }

    public JenkinsTemplate(String image, String namespace) {
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        Path path = Path.of(CommonConstant.ROOT_PATH, namespace, "templates", "jenkins");
        try {
            FileHelper.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("JENKINS", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "JENKINS_IMAGE", image,
                                "VOLUME_PATH", path.toString()),
                        String.class
                );
    }

}
