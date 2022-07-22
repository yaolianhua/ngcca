package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.common.api.UUIDGenerator;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.kubernetes.client.openapi.ApiException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildPackApiV2IT extends BuildPackIntegrationTestBase {

    @Autowired
    private BuildPackApiV2 buildPackApiV2;
    @Autowired
    private KubectlApi kubectlApi;
    @Autowired
    private NamespaceApi namespaceApi;
    public final String namespace = UUIDGenerator.uuidNoDash();

    @Test
    public void apply() throws InterruptedException, ApiException {
        AtomicInteger loopCount = new AtomicInteger(0);

        namespaceApi.create(namespace);

        BuildPack buildPack = buildPackApiV2.apply(
                namespace,
                "https://gitee.com/yannanshan/devops-thymeleaf.git",
                "master");

        System.out.println("\n***************************** Print Kaniko Job Yaml Start ******************************\n");
        System.out.println(buildPack.getYaml());
        System.out.println("\n***************************** Print Kaniko Job Yaml End ******************************\n");

        while (loopCount.get() < 20){
            TimeUnit.SECONDS.sleep(6);
            BuildPackApiV2.KanikoStatus status = buildPackApiV2.getStatus(namespace, buildPack.getJobResource().getName());

            if (Objects.equals(status,BuildPackApiV2.KanikoStatus.Unknown)) {
                System.out.println("Kaniko status is [Unknown]");
            } else if (Objects.equals(status,BuildPackApiV2.KanikoStatus.Ready)) {
                System.out.println("Kaniko status is [Ready]");
            } else if (Objects.equals(status,BuildPackApiV2.KanikoStatus.Active)) {
                System.out.println("Kaniko status is [Active]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status,BuildPackApiV2.KanikoStatus.Failed)) {
                System.out.println("Kaniko status is [Failed]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
                cleared(buildPack);
            } else if (Objects.equals(status,BuildPackApiV2.KanikoStatus.Succeeded)) {
                System.out.printf("Kaniko status is [Succeeded] imagebuild artifact url [%s]%n", buildPack.getArtifact());
                printKanikoLog(namespace, buildPack.getJobResource().getName());

                cleared(buildPack);
                return;
            }

            loopCount.incrementAndGet();
        }

        System.out.println("Kaniko job has been timeout.");
        printKanikoLog(namespace, buildPack.getJobResource().getName());
        cleared(buildPack);
    }

    private void printKanikoLog(String namespace, String job){
        System.out.println("\n***************************** Print Kaniko Job log Start ******************************\n");
        System.out.println(buildPackApiV2.fetchLog(namespace, job));
        System.out.println("\n***************************** Print Kaniko Job log End ******************************\n");
    }

    private void cleared(BuildPack buildPack) throws ApiException {
        Boolean delete = kubectlApi.delete(namespace, buildPack.getYaml());
        System.out.printf("Delete kaniko job [%s]%n", delete);
        namespaceApi.delete(namespace);
        System.out.printf("Delete namespace [%s]%n", namespace);
    }
}
