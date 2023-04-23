package io.hotcloud.vendor.kaniko.model;

import lombok.Data;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class KanikoJobExpressionVariable {

    /**
     * k8s namespace
     */
    private String namespace;
    /**
     * custom business id
     */
    private String businessId;
    /**
     * kaniko job name
     */
    private String job;
    /**
     * kaniko secret name
     */
    private String secret;
    /**
     * kaniko args, e.g. harbor.local:5000/image-build-test/jenkins-xxxxxx:timestamp
     */
    private String destination;
    /**
     * kaniko executor image e.g. harbor.local:5000/library/kaniko:latest
     */
    private String kaniko;
    /**
     * alpine init container image e.g. harbor.local:5000/library/alpine:latest
     */
    private String initAlpineContainer;
    /**
     * base64-encoded Dockerfile
     */
    private String encodedDockerfile;
    /**
     * container hostAliases mapping
     * <p> key --> ip address e.g. 10.0.0.189
     * <p> value --> collections of hostnames e.g. [git.docker.local, harbor.local]
     */
    private Map<String, List<String>> hostAliases;

    private GitExpressionVariable git;

    public static KanikoJobExpressionVariable of(String businessId,
                                                 String namespace,
                                                 String job,
                                                 String secret,
                                                 String destination,
                                                 String kaniko,
                                                 String initAlpineContainer,
                                                 String encodedDockerfile,
                                                 GitExpressionVariable git,
                                                 Map<String, List<String>> hostAliases) {

        Assert.hasText(businessId, "business ID is null");
        Assert.hasText(namespace, "namespace is null");
        Assert.hasText(job, "job name is null");
        Assert.hasText(secret, "secret name is null");
        Assert.hasText(destination, "kaniko args [-- destination] is null");
        Assert.hasText(kaniko, "kaniko image is null");
        Assert.hasText(initAlpineContainer, "init alpine container image is null");
        Assert.hasText(encodedDockerfile, "dockerfile is null");

        KanikoJobExpressionVariable expressionVariable = new KanikoJobExpressionVariable();
        expressionVariable.setBusinessId(businessId);
        expressionVariable.setNamespace(namespace);
        expressionVariable.setJob(job);
        expressionVariable.setSecret(secret);
        expressionVariable.setDestination(destination);
        expressionVariable.setEncodedDockerfile(encodedDockerfile);
        expressionVariable.setKaniko(kaniko);
        expressionVariable.setInitAlpineContainer(initAlpineContainer);
        expressionVariable.setGit(git);
        expressionVariable.setHostAliases(hostAliases);

        return expressionVariable;
    }

    public boolean hasGit() {
        return Objects.nonNull(git);
    }

    @Data
    public static class GitExpressionVariable {
        /**
         * 如果是从仓库构建，需要指定仓库的分支信息， 默认为 {@code master}
         */
        private String branch;
        /**
         * 如果是从仓库构建，需要指定仓库的地址，只支持http(s)协议 e.g. <a href="https://git.docker.local/self-host/thymeleaf-fragments.git">thymeleaf-fragments</a>
         */
        private String httpGitUrl;
        /**
         * git init container image e.g. harbor.local:5000/library/alpine-git:latest
         */
        private String initGitContainer;

        public static GitExpressionVariable of(String gitUrl, String branch, String initGitContainer) {

            Assert.hasText(gitUrl, "http git url is null");
            Assert.hasText(branch, "git branch is null");
            Assert.hasText(initGitContainer, "init git container image null");

            GitExpressionVariable gitExpressionVariable = new GitExpressionVariable();
            gitExpressionVariable.setHttpGitUrl(gitUrl);
            gitExpressionVariable.setBranch(branch);
            gitExpressionVariable.setInitGitContainer(initGitContainer);

            return gitExpressionVariable;
        }
    }
}
