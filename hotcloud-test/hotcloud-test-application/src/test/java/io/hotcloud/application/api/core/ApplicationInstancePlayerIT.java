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
                                .url("192.168.146.128:5000/library/nginx:v1")
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
                System.err.println("Application instance [" + fetched.getName() + "] create failed");
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
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
                                .url("http://192.168.146.128:28080/yaolianhua/java/kaniko-test/jenkins.war")
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
                System.err.println("Application instance [" + fetched.getName() + "] create failed");
                break;
            }
        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
    }
    @Test
    public void playJarDeployment() throws InterruptedException {
        ApplicationForm form = ApplicationForm.builder()
                .name("web")
                .canHttp(true)
                .replicas(1)
                .serverPort(4000)
                .source(
                        ApplicationInstanceSource.builder()
                                .url("http://192.168.146.128:28080/yaolianhua/java/kaniko-test/web.jar")
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
                System.err.println("Application instance [" + fetched.getName() + "] create failed");
                break;
            }

        }

        TimeUnit.SECONDS.sleep(3);
        System.err.println("after 3 seconds, application instance [" + instance.getName() + "] will be delete");

        player.delete(instance.getId());
    }

}
