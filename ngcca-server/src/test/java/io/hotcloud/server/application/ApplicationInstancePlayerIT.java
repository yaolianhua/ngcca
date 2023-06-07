package io.hotcloud.server.application;

import io.hotcloud.module.application.*;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.server.CoreServerApplication;
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
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = CoreServerApplication.class
)
@ActiveProfiles("test")
public class ApplicationInstancePlayerIT  {

    @Autowired
    private ApplicationInstancePlayer player;
    @Autowired
    private UserApi userApi;
    @Autowired
    private ApplicationInstanceService applicationInstanceService;

    @Before
    public void before() {
        UserDetails adminUserDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken adminUsernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    @Test
    public void playImageDeployment() throws InterruptedException {
        ApplicationForm form = ApplicationForm.builder()
                .name("nginx")
                .canHttp(true)
                .replicas(3)
                .serverPort(80)
                .source(
                        ApplicationInstanceSource.builder()
                                .url("harbor.local:5000/library/nginx:latest")
                                .origin(ApplicationInstanceSource.Origin.IMAGE)
                                .build()
                ).build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "nginx");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())) {
                System.err.println("Application instance [" + fetched.getName() + "] create failed \n" + fetched.getMessage());
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void playWarDeployment() throws InterruptedException {
        ApplicationForm form = ApplicationForm.builder()
                .name("jenkins")
                .canHttp(true)
                .replicas(1)
                .serverPort(8080)
                .source(
                        ApplicationInstanceSource.builder()
                                .url("http://minio.docker.local:9009/files/jenkins.war")
                                .origin(ApplicationInstanceSource.Origin.WAR)
                                .build()
                ).build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "jenkins");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())) {
                System.err.println("Application instance [" + fetched.getName() + "] create failed \n" + fetched.getMessage());
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void playJarDeployment() throws InterruptedException {
        ApplicationForm form = ApplicationForm.builder()
                .name("thymeleaf-fragments")
                .canHttp(true)
                .replicas(1)
                .serverPort(8080)
                .envs(Map.of())
                .source(
                        ApplicationInstanceSource.builder()
                                .url("http://minio.docker.local:9009/files/thymeleaf-fragments.jar")
                                .startArgs("-Dspring.profiles.active=production")
                                .startOptions("-Xms128m -Xmx512m")
                                .origin(ApplicationInstanceSource.Origin.JAR)
                                .build()
                ).build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "thymeleaf-fragments");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())) {
                System.err.println("Application instance [" + fetched.getName() + "] create failed \n" + fetched.getMessage());
                break;
            }

        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
        TimeUnit.SECONDS.sleep(10);
    }

}
