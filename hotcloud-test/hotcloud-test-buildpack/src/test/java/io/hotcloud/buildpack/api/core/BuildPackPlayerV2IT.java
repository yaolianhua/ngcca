package io.hotcloud.buildpack.api.core;

import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
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
    public void play() throws InterruptedException {
        BuildPack buildPack = buildPackPlayerV2.play(
                "https://gitee.com/yannanshan/devops-thymeleaf.git",
                "master"
        );

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            BuildPack one = buildPackService.findOne(buildPack.getId());
            if (one.isDone() && BuildPackConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Git repository [%s][%s] build successful. artifact url [%s]%n",
                        one.getHttpGitUrl(), one.getGitBranch(), one.getArtifact());
                System.out.println("Kaniko logs print: \n" + one.getLogs());

                break;
            }

            if (one.isDone() && !BuildPackConstant.SUCCESS_MESSAGE.equals(one.getMessage())) {
                System.out.printf("Git repository [%s][%s] build failed%n",
                        one.getHttpGitUrl(), one.getGitBranch());
                System.out.println("Build message: \n" + one.getLogs());

                break;
            }
        }
    }
}
