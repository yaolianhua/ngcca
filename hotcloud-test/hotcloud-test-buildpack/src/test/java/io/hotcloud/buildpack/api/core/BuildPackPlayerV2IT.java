package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.common.api.CommonConstant;
import io.hotcloud.security.api.user.UserApi;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.concurrent.TimeUnit;

public class BuildPackPlayerV2IT extends BuildPackIntegrationTestBase {

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
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    @Test
    public void playFromWarArtifact() throws InterruptedException {
        BuildPack buildPack = buildPackPlayerV2.play(
                BuildImage.ofWar("http://192.168.146.128:28080/yaolianhua/java/kaniko-test/jenkins.war")
        );

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
    public void playFromJarArtifact() throws InterruptedException {
        BuildPack buildPack = buildPackPlayerV2.play(
                BuildImage.ofJar("http://192.168.146.128:28080/yaolianhua/java/kaniko-test/web.jar",
                        "-Xms128m -Xmx512m",
                        "-Dspring.profiles.active=production")
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
    public void playFromSourceCode() throws InterruptedException {
        BuildPack buildPack = buildPackPlayerV2.play(
                BuildImage.ofSource("https://gitee.com/yannanshan/devops-thymeleaf.git",
                        "master")
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
