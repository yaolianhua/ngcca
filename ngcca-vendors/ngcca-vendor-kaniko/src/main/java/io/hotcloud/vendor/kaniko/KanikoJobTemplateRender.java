package io.hotcloud.vendor.kaniko;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import io.hotcloud.vendor.kaniko.model.JobExpressionVariable;
import io.hotcloud.vendor.kaniko.model.JobTemplateObject;
import io.hotcloud.vendor.kaniko.model.SecretExpressionVariable;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KanikoJobTemplateRender {

    public static final String SOURCE_CODE_TEMPLATE_YAML;
    public static final String ARTIFACT_TEMPLATE_YAML;

    public static final String SECRET_TEMPLATE_YAML;
    private static final ObjectMapper yamlObjectMapper;

    static {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.disable(YAMLGenerator.Feature.SPLIT_LINES);
        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);

        yamlObjectMapper = Jackson2ObjectMapperBuilder.json()
                .factory(yamlFactory)
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .build();

        try {
            SOURCE_CODE_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("sourcecode-template.yaml").getInputStream())).lines().collect(Collectors.joining("\n"));
            ARTIFACT_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("artifact-template.yaml").getInputStream())).lines().collect(Collectors.joining("\n"));
            SECRET_TEMPLATE_YAML = new BufferedReader(new InputStreamReader(new ClassPathResource("secret-template.yaml").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 从模板创建可直接部署的k8s的job资源对象
     * <p>1. 从Git克隆创建的模板
     * <P>2. 从给定包创建的模板
     *
     * @param job {@link JobExpressionVariable}
     * @return job yaml
     */
    @SneakyThrows
    public static String parseJob(JobExpressionVariable job) {

        HashMap<String, String> renders = new HashMap<>(32);

        renders.put(Kaniko.NAMESPACE, job.getNamespace());
        renders.put(Kaniko.ID, job.getBusinessId());
        renders.put(Kaniko.JOB_NAME, job.getJob());
        renders.put(Kaniko.LABEL_NAME, job.getJob());
        renders.put(Kaniko.SECRET_NAME, job.getSecret());
        renders.put(Kaniko.DESTINATION, job.getDestination());
        renders.put(Kaniko.KANIKO_IMAGE, job.getKaniko());
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_IMAGE, job.getInitAlpineContainer());
        renders.put(Kaniko.DOCKERFILE_ENCODED, job.getEncodedDockerfile());
        renders.put(Kaniko.INIT_ALPINE_CONTAINER_NAME, "alpine");
        renders.put(Kaniko.KANIKO_CONTAINER_NAME, "kaniko");
        renders.put(Kaniko.GIT_BRANCH, Objects.nonNull(job.getGit()) ? job.getGit().getBranch() : null);
        renders.put(Kaniko.HTTP_GIT_URL, Objects.nonNull(job.getGit()) ? job.getGit().getHttpGitUrl() : null);
        renders.put(Kaniko.INIT_GIT_CONTAINER_IMAGE, Objects.nonNull(job.getGit()) ? job.getGit().getInitGitContainer() : null);
        renders.put(Kaniko.INIT_GIT_CONTAINER_NAME, "git");

        String template = job.hasGit() ? SOURCE_CODE_TEMPLATE_YAML : ARTIFACT_TEMPLATE_YAML;
        String yaml = TemplateRender.apply(template, renders);

        JobTemplateObject jobTemplate = yamlObjectMapper.readValue(yaml, new TypeReference<>() {
        });

        List<JobTemplateObject.HostAliases> hostAliases = job.getHostAliases().entrySet().stream()
                .map(e -> new JobTemplateObject.HostAliases(e.getKey(), e.getValue()))
                .toList();
        jobTemplate.getSpec().getTemplate().getSpec().setHostAliases(hostAliases);

        yaml = yamlObjectMapper.writeValueAsString(jobTemplate);
        return yaml;
    }

    /**
     * 从模板创建secret
     */
    @SneakyThrows
    public static String parseSecret(SecretExpressionVariable secret) {

        Map<String, String> renders = new HashMap<>(8);

        renders.put(Kaniko.NAMESPACE, secret.getNamespace());
        renders.put(Kaniko.SECRET_NAME, secret.getSecret());
        renders.put(Kaniko.LABEL_NAME, secret.getSecret());
        renders.put(Kaniko.DOCKER_CONFIG_JSON, secret.getDockerconfigjson());

        return TemplateRender.apply(SECRET_TEMPLATE_YAML, renders);
    }

    /**
     * Kaniko 模板变量名
     */
    interface Kaniko {
        String NAMESPACE = "NAMESPACE";
        String ID = "ID";
        String JOB_NAME = "JOB_NAME";
        String INIT_GIT_CONTAINER_NAME = "INIT_GIT_CONTAINER_NAME";
        String INIT_ALPINE_CONTAINER_NAME = "INIT_ALPINE_CONTAINER_NAME";
        String LABEL_NAME = "LABEL_NAME";
        String SECRET_NAME = "SECRET_NAME";
        String DESTINATION = "DESTINATION";
        String GIT_BRANCH = "GIT_BRANCH";
        String HTTP_GIT_URL = "HTTP_GIT_URL";
        String KANIKO_IMAGE = "KANIKO_IMAGE";
        String KANIKO_CONTAINER_NAME = "KANIKO_CONTAINER_NAME";
        String INIT_GIT_CONTAINER_IMAGE = "INIT_GIT_CONTAINER_IMAGE";
        String INIT_ALPINE_CONTAINER_IMAGE = "INIT_ALPINE_CONTAINER_IMAGE";
        String DOCKERFILE_ENCODED = "DOCKERFILE_ENCODED";
        String DOCKER_CONFIG_JSON = "DOCKER_CONFIG_JSON";
    }
}
