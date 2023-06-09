package io.hotcloud.server.buildpack;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.JavaRuntime;
import io.hotcloud.common.utils.UUIDGenerator;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.module.buildpack.BuildPackApi;
import io.hotcloud.module.buildpack.model.BuildImage;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.buildpack.model.BuildPackConstant;
import io.hotcloud.module.buildpack.model.JobState;
import io.hotcloud.server.CoreServerApplication;
import io.hotcloud.vendor.minio.MinioProperties;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoreServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class BuildPackApiIT {

    @Autowired
    private BuildPackApi buildPackApi;
    @Autowired
    private KubectlClient kubectlApi;
    @Autowired
    private NamespaceClient namespaceApi;
    @Autowired
    private MinioProperties minioProperties;

    public final String namespace = UUIDGenerator.uuidNoDash();

    @After
    public void after() throws ApiException {
        namespaceApi.delete(namespace);
        Log.info(this, namespace, "delete namespace");
    }

    @Test
    public void jarArtifactApply() throws ApiException {

        AtomicBoolean succeeded = new AtomicBoolean(false);
        //manual upload
        String jarUrl = minioProperties.getEndpoint() + "/files/thymeleaf-fragments.jar";
        BuildImage jar = BuildImage.ofJar(jarUrl,
                "-Xms128m -Xmx512m",
                "-Dspring.profiles.active=production",
                JavaRuntime.JAVA11);

        //create namespace
        namespaceApi.create(namespace);
        //apply
        BuildPack buildPack = buildPackApi.apply(namespace, jar);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            JobState status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());
            boolean succeed = Objects.equals(status, JobState.SUCCEEDED);
            if (succeed) {
                succeeded.set(true);
            }
            return succeed;
        });

        Assertions.assertTrue(succeeded.get());
        Log.info(this, buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT), "image build artifact url");
        //
        System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
        System.out.println(buildPackApi.fetchLog(namespace, buildPack.getJobResource().getName()));
        //
        Boolean deleted = kubectlApi.delete(namespace, YamlBody.of(buildPack.getYaml()));
        Log.info(this, deleted, "delete kaniko job");
    }

    @Test
    public void warArtifactApply() throws ApiException {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        //manual upload file
        String warUrl = minioProperties.getEndpoint() + "/files/jenkins.war";
        BuildImage war = BuildImage.ofWar(warUrl, JavaRuntime.JAVA17);
        //create namespace
        namespaceApi.create(namespace);
        //apply
        BuildPack buildPack = buildPackApi.apply(namespace, war);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            JobState status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());
            boolean succeed = Objects.equals(status, JobState.SUCCEEDED);
            if (succeed) {
                succeeded.set(true);
            }
            return succeed;
        });

        Assertions.assertTrue(succeeded.get());
        Log.info(this, buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT), "image build artifact url");
        //
        System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
        System.out.println(buildPackApi.fetchLog(namespace, buildPack.getJobResource().getName()));
        //
        Boolean deleted = kubectlApi.delete(namespace, YamlBody.of(buildPack.getYaml()));
        Log.info(this, deleted, "delete kaniko job");
    }

    @Test
    public void sourceApply() throws ApiException {
        AtomicBoolean succeeded = new AtomicBoolean(false);

        BuildImage code = BuildImage.ofSource(
                "https://gitlab.com/yaolianhua/devops-jdk11.git",
                "master",
                "",
                "-Xms128m -Xmx512m",
                "-Dspring.profiles.active=container",
                JavaRuntime.JAVA11);
        //create namespace
        namespaceApi.create(namespace);
        //apply
        BuildPack buildPack = buildPackApi.apply(namespace, code);

        await().atMost(20, TimeUnit.MINUTES).until(() -> {
            JobState status = buildPackApi.getStatus(namespace, buildPack.getJobResource().getName());
            boolean succeed = Objects.equals(status, JobState.SUCCEEDED);
            if (succeed) {
                succeeded.set(true);
            }
            return succeed;
        });

        Assertions.assertTrue(succeeded.get());
        Log.info(this, buildPack.getAlternative().get(BuildPackConstant.IMAGEBUILD_ARTIFACT), "image build artifact url");
        //
        System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
        System.out.println(buildPackApi.fetchLog(namespace, buildPack.getJobResource().getName()));
        //
        Boolean deleted = kubectlApi.delete(namespace, YamlBody.of(buildPack.getYaml()));
        Log.info(this, deleted, "delete kaniko job");
    }

}
