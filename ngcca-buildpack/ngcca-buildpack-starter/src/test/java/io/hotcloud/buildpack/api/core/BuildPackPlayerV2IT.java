package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.NgccaBuildPackApplication;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.RuntimeImages;
import io.hotcloud.security.api.user.UserApi;
import org.junit.Before;
import org.junit.Test;
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
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaBuildPackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BuildPackPlayerV2IT {

    @Autowired
    private BuildPackPlayerV2 buildPackPlayerV2;
    @Autowired
    private BuildPackService buildPackService;
    @Autowired
    private UserApi userApi;

    @Before
    public void before() {
        UserDetails adminUserDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken adminUsernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    @Test
    public void playWarArtifact() throws InterruptedException {
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId)
                .collect(Collectors.toList());
        for (String buildPackId : buildPackIds) {
            buildPackPlayerV2.delete(buildPackId, false);
        }
        BuildPack buildPack = buildPackPlayerV2.play(BuildImage.ofWar("http://minio.docker.local:9009/files/jenkins.war", RuntimeImages.Java11));

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("War package [%s] build successful. artifact url [%s]%n",
                        one.getPackageUrl(), one.getArtifact());
                System.out.println("Kaniko logs print: \n" + one.getLogs());

                break;
            }

            if (one.isDone() && !CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("War package [%s] build failed%n",
                        one.getPackageUrl());
                System.out.println("Build message: \n" + one.getLogs());

                break;
            }
        }
    }

    @Test
    public void playJarArtifact() throws InterruptedException {
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId)
                .collect(Collectors.toList());
        for (String buildPackId : buildPackIds) {
            buildPackPlayerV2.delete(buildPackId, false);
        }

        BuildPack buildPack = buildPackPlayerV2.play(
                BuildImage.ofJar("http://minio.docker.local:9009/files/thymeleaf-fragments.jar",
                        "-Xms128m -Xmx512m",
                        "-Dspring.profiles.active=production", RuntimeImages.Java11)
        );

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Jar package [%s] build successful. artifact url [%s]%n",
                        one.getPackageUrl(), one.getArtifact());
                System.out.println("Kaniko logs print: \n" + one.getLogs());

                break;
            }

            if (one.isDone() && !CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Jar package [%s] build failed%n",
                        one.getPackageUrl());
                System.out.println("Build message: \n" + one.getLogs());

                break;
            }
        }
    }

    @Test
    public void playSourceCode() throws InterruptedException {
        List<String> buildPackIds = buildPackService.findAll("admin")
                .stream()
                .filter(e -> !e.isDone())
                .map(BuildPack::getId)
                .collect(Collectors.toList());
        for (String buildPackId : buildPackIds) {
            buildPackPlayerV2.delete(buildPackId, false);
        }
        BuildPack buildPack = buildPackPlayerV2.play(
                BuildImage.ofSource("https://git.docker.local/self-host/thymeleaf-fragments.git",
                        "master", "", "", "", RuntimeImages.Java11)
        );

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Git repository [%s][%s] build successful. artifact url [%s]%n",
                        one.getHttpGitUrl(), one.getGitBranch(), one.getArtifact());
                System.out.println("Kaniko logs print: \n" + one.getLogs());

                break;
            }

            if (one.isDone() && !CommonConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Git repository [%s][%s] build failed%n",
                        one.getHttpGitUrl(), one.getGitBranch());
                System.out.println("Build message: \n" + one.getLogs());

                break;
            }
        }
    }
}
