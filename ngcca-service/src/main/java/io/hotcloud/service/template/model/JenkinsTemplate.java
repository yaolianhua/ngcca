package io.hotcloud.service.template.model;

import io.hotcloud.service.template.Template;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private String storageNode;
    private String service = Template.JENKINS.name().toLowerCase();

    public JenkinsTemplate(String namespace) {
        this.namespace = namespace;
    }

    public JenkinsTemplate(String image, String namespace, String storageNode) {
        Assert.hasText(storageNode, "storage node name is null");
        if (StringUtils.hasText(image)) {
            this.image = image;
        }
        this.storageNode = storageNode;
        this.namespace = namespace;
    }

    public String getYaml(String id) {
        return new SpelExpressionParser()
                .parseExpression(TEMPLATE, new TemplateParserContext())
                .getValue(
                        Map.of("JENKINS", name,
                                "ID", id,
                                "NAMESPACE", namespace,
                                "JENKINS_IMAGE", image,
                                "STORAGE_NODE", storageNode),
                        String.class
                );
    }

}
