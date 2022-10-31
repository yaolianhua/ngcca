package io.hotcloud.application.api.core;

import io.hotcloud.application.ApplicationIntegrationTestBase;
import io.hotcloud.security.api.user.UserApi;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApplicationInstancePlayerIT extends ApplicationIntegrationTestBase {

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
        if (one != null){
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()){
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())){
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
        if (one != null){
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()){
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())){
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
                .name("web")
                .canHttp(true)
                .replicas(1)
                .serverPort(4000)
                .envs(Map.of("hotcloud.host", "hotcloud",
                        "hotcloud.port", "8080"))
                .source(
                        ApplicationInstanceSource.builder()
                                .url("http://minio.docker.local:9009/files/web.jar")
                                .startArgs("-Dspring.profiles.active=production")
                                .startOptions("-Xms128m -Xmx512m")
                                .origin(ApplicationInstanceSource.Origin.JAR)
                                .build()
                ).build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "web");
        if (one != null){
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        while (true) {
            TimeUnit.SECONDS.sleep(3);
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()){
                System.err.println("Application instance [" + fetched.getName() + "] create success");
                break;
            }
            if (!fetched.isSuccess() && StringUtils.hasText(fetched.getMessage())){
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
