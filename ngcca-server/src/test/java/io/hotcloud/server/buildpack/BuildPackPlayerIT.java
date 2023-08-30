package io.hotcloud.server.buildpack;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.JavaRuntime;
import io.hotcloud.server.ServerApplication;
import io.hotcloud.service.buildpack.BuildPackPlayer;
import io.hotcloud.service.buildpack.BuildPackService;
import io.hotcloud.service.buildpack.model.BuildImage;
import io.hotcloud.service.buildpack.model.BuildPack;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.vendor.minio.MinioProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class BuildPackPlayerIT {

    @Autowired
    private BuildPackPlayer buildPackPlayer;
    @Autowired
    private BuildPackService buildPackService;
    @Autowired
    private UserApi userApi;
    @Autowired
    private MinioProperties minioProperties;

    @Before
    public void before() {
        UserDetails adminUserDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken adminUsernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    @Test
    public void playWarArtifact() {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId)
                .toList();
        for (String buildPackId : buildPackIds) {
            buildPackPlayer.delete(buildPackId, false);
        }
        String warUrl = minioProperties.getEndpoint() + "/files/jenkins.war";
        BuildImage war = BuildImage.ofWar(warUrl, JavaRuntime.JAVA17);
        BuildPack buildPack = buildPackPlayer.play(war);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equalsIgnoreCase(one.getMessage())) {
                Log.info(this, one.getArtifact(), "image build artifact url");
                //
                System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
                System.out.println(one.getLogs());
                succeeded.set(true);
            }

            return succeeded.get();
        });
        Assertions.assertTrue(succeeded.get());
    }


    @Test
    public void playJarArtifact() {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId)
                .toList();
        for (String buildPackId : buildPackIds) {
            buildPackPlayer.delete(buildPackId, false);
        }

        String jarUrl = minioProperties.getEndpoint() + "/files/thymeleaf-fragments.jar";
        BuildImage jar = BuildImage.ofJar(jarUrl, "-Xms128m -Xmx512m", "-Dspring.profiles.active=production", JavaRuntime.JAVA11);
        BuildPack buildPack = buildPackPlayer.play(jar);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equalsIgnoreCase(one.getMessage())) {
                Log.info(this, one.getArtifact(), "image build artifact url");
                //
                System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
                System.out.println(one.getLogs());
                succeeded.set(true);
            }

            return succeeded.get();
        });

        Assertions.assertTrue(succeeded.get());
    }

    @Test
    public void playSourceCode() {
        AtomicBoolean succeeded = new AtomicBoolean(false);
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId).toList();
        for (String buildPackId : buildPackIds) {
            buildPackPlayer.delete(buildPackId, false);
        }

        BuildImage code = BuildImage.ofSource(
                "https://gitlab.com/yaolianhua/devops-jdk11.git",
                "master",
                "",
                "-Xms128m -Xmx512m",
                "-Dspring.profiles.active=container",
                JavaRuntime.JAVA11);
        BuildPack buildPack = buildPackPlayer.play(code);

        await().atMost(20, TimeUnit.MINUTES).until(() -> {
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equalsIgnoreCase(one.getMessage())) {
                Log.info(this, one.getArtifact(), "image build artifact url");
                //
                System.out.println("\n***************************** Print Kaniko Job log ******************************\n");
                System.out.println(one.getLogs());
                succeeded.set(true);
            }

            return succeeded.get();
        });

        Assertions.assertTrue(succeeded.get());
    }
}
