package io.hotcloud.server.buildpack;

import io.hotcloud.common.model.JavaRuntime;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.*;
import io.hotcloud.server.NgccaCoreServerApplication;
import io.kubernetes.client.openapi.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaCoreServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BuildPackApiIT {

    @Autowired
    private BuildPackApi buildPackApi;
    @Autowired
    private KubectlClient kubectlApi;
    @Autowired
    private NamespaceClient namespaceApi;
    public final String namespace = UUIDGenerator.uuidNoDash();

    @Test
    public void jarArtifactApply() throws ApiException, InterruptedException {
        AtomicInteger loopCount = new AtomicInteger(0);

        namespaceApi.create(namespace);
        BuildPack buildPack = buildPackApi.apply(
                namespace,
                BuildImage.ofJar("http://minio.docker.local:9009/files/thymeleaf-fragments.jar",
                        "-Xms128m -Xmx512m",
                        "-Dspring.profiles.active=production", JavaRuntime.JAVA11));

        System.out.println("\n***************************** Print Kaniko Job Yaml Start ******************************\n");
        System.out.println(buildPack.getYaml());
        System.out.println("\n***************************** Print Kaniko Job Yaml End ******************************\n");

        while (loopCount.get() < 60) {
            TimeUnit.SECONDS.sleep(6);
            ImageBuildStatus status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());

            if (Objects.equals(status, ImageBuildStatus.Unknown)) {
                System.out.println("Kaniko status is [Unknown]");
            } else if (Objects.equals(status, ImageBuildStatus.Ready)) {
                System.out.println("Kaniko status is [Ready]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Active)) {
                System.out.println("Kaniko status is [Active]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Failed)) {
                System.out.println("Kaniko status is [Failed]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
                cleared(buildPack);
                return;
            } else if (Objects.equals(status, ImageBuildStatus.Succeeded)) {
                System.out.printf("Kaniko status is [Succeeded] imagebuild artifact url [%s]%n",
                        buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
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

    @Test
    public void warArtifactApply() throws ApiException, InterruptedException {
        AtomicInteger loopCount = new AtomicInteger(0);

        namespaceApi.create(namespace);
        BuildPack buildPack = buildPackApi.apply(
                namespace,
                BuildImage.ofWar("http://minio.docker.local:9009/files/jenkins.war", JavaRuntime.JAVA11));

        System.out.println("\n***************************** Print Kaniko Job Yaml Start ******************************\n");
        System.out.println(buildPack.getYaml());
        System.out.println("\n***************************** Print Kaniko Job Yaml End ******************************\n");

        while (loopCount.get() < 60) {
            TimeUnit.SECONDS.sleep(6);
            ImageBuildStatus status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());

            if (Objects.equals(status, ImageBuildStatus.Unknown)) {
                System.out.println("Kaniko status is [Unknown]");
            } else if (Objects.equals(status, ImageBuildStatus.Ready)) {
                System.out.println("Kaniko status is [Ready]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Active)) {
                System.out.println("Kaniko status is [Active]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Failed)) {
                System.out.println("Kaniko status is [Failed]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
                cleared(buildPack);
                return;
            } else if (Objects.equals(status, ImageBuildStatus.Succeeded)) {
                System.out.printf("Kaniko status is [Succeeded] imagebuild artifact url [%s]%n",
                        buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
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

    @Test
    public void sourceApply() throws InterruptedException, ApiException {
        AtomicInteger loopCount = new AtomicInteger(0);

        namespaceApi.create(namespace);

        BuildPack buildPack = buildPackApi.apply(
                namespace,
                BuildImage.ofSource("https://git.docker.local/self-host/thymeleaf-fragments.git",
                        "master", "", "", "", JavaRuntime.JAVA11));

        System.out.println("\n***************************** Print Kaniko Job Yaml Start ******************************\n");
        System.out.println(buildPack.getYaml());
        System.out.println("\n***************************** Print Kaniko Job Yaml End ******************************\n");

        while (loopCount.get() < 60) {
            TimeUnit.SECONDS.sleep(60);
            ImageBuildStatus status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());

            if (Objects.equals(status, ImageBuildStatus.Unknown)) {
                System.out.println("Kaniko status is [Unknown]");
            } else if (Objects.equals(status, ImageBuildStatus.Ready)) {
                System.out.println("Kaniko status is [Ready]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Active)) {
                System.out.println("Kaniko status is [Active]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
            } else if (Objects.equals(status, ImageBuildStatus.Failed)) {
                System.out.println("Kaniko status is [Failed]");
                printKanikoLog(namespace, buildPack.getJobResource().getName());
                cleared(buildPack);
                return;
            } else if (Objects.equals(status, ImageBuildStatus.Succeeded)) {
                System.out.printf("Kaniko status is [Succeeded] imagebuild artifact url [%s]%n",
                        buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT));
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

    private void printKanikoLog(String namespace, String job) {
        System.out.println("\n***************************** Print Kaniko Job log Start ******************************\n");
        System.out.println(buildPackApi.fetchLog(namespace, job));
    }

    private void cleared(BuildPack buildPack) throws ApiException {
        Boolean delete = kubectlApi.delete(namespace, YamlBody.of(buildPack.getYaml()));
        System.out.printf("Delete kaniko job [%s]%n", delete);
        namespaceApi.delete(namespace);
        System.out.printf("Delete namespace [%s]%n", namespace);
    }
}
