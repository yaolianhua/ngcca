package io.hotcloud.server.application;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.JavaRuntime;
import io.hotcloud.server.ServerApplication;
import io.hotcloud.service.application.*;
import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.application.model.ApplicationInstanceSource;
import io.hotcloud.service.registry.SystemRegistryProperties;
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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = ServerApplication.class
)
@ActiveProfiles("test")
public class ApplicationInstancePlayerIT {

    @Autowired
    private ApplicationInstancePlayer player;
    @Autowired
    private UserApi userApi;
    @Autowired
    private ApplicationInstanceService applicationInstanceService;
    @Autowired
    private SystemRegistryProperties systemRegistryProperties;
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
    public void playImageApplication() {
        ApplicationInstanceSource imageSource = ApplicationInstanceSource.builder()
                .url(systemRegistryProperties.getUrl() + "/library/nginx:latest")
                .origin(ApplicationInstanceSource.Origin.IMAGE)
                .build();
        ApplicationForm form = ApplicationForm.builder()
                .name("nginx")
                .canHttp(true)
                .replicas(3)
                .serverPort(80)
                .source(imageSource)
                .build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "nginx");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        AtomicBoolean succeed = new AtomicBoolean(false);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                succeed.set(true);
            }

            return succeed.get();
        });

        Assertions.assertTrue(succeed.get());
        Log.info(this, instance.getName(), "application create success");


        player.delete(instance.getId());
        AtomicBoolean deleted = new AtomicBoolean(false);
        await().atMost(1, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isDeleted()) {
                deleted.set(true);
            }

            return deleted.get();
        });
        Assertions.assertTrue(deleted.get());
        Log.info(this, instance.getName(), "application delete success");
    }

    @Test
    public void playWarApplication() {
        ApplicationInstanceSource warSource = ApplicationInstanceSource.builder()
                .url(minioProperties.getEndpoint() + "/files/jenkins.war")
                .origin(ApplicationInstanceSource.Origin.WAR)
                .runtime(JavaRuntime.JAVA11)
                .build();
        ApplicationForm form = ApplicationForm.builder()
                .name("jenkins")
                .canHttp(true)
                .replicas(1)
                .serverPort(8080)
                .source(warSource)
                .build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "jenkins");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        AtomicBoolean succeed = new AtomicBoolean(false);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                succeed.set(true);
            }

            return succeed.get();
        });

        Assertions.assertTrue(succeed.get());
        Log.info(this, instance.getName(), "application create success");


        player.delete(instance.getId());
        AtomicBoolean deleted = new AtomicBoolean(false);
        await().atMost(1, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isDeleted()) {
                deleted.set(true);
            }

            return deleted.get();
        });
        Assertions.assertTrue(deleted.get());
        Log.info(this, instance.getName(), "application delete success");
    }

    @Test
    public void playJarApplication() {
        ApplicationInstanceSource jarSource = ApplicationInstanceSource.builder()
                .url(minioProperties.getEndpoint() + "/files/thymeleaf-fragments.jar")
                .startArgs("-Dspring.profiles.active=production")
                .startOptions("-Xms128m -Xmx512m")
                .origin(ApplicationInstanceSource.Origin.JAR)
                .runtime(JavaRuntime.JAVA11)
                .build();
        ApplicationForm form = ApplicationForm.builder()
                .name("thymeleaf-fragments")
                .canHttp(true)
                .replicas(1)
                .serverPort(8080)
                .envs(Map.of())
                .source(jarSource)
                .build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "thymeleaf-fragments");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        AtomicBoolean succeed = new AtomicBoolean(false);

        await().atMost(5, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                succeed.set(true);
            }

            return succeed.get();
        });

        Assertions.assertTrue(succeed.get());
        Log.info(this, instance.getName(), "application create success");


        player.delete(instance.getId());
        AtomicBoolean deleted = new AtomicBoolean(false);
        await().atMost(1, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isDeleted()) {
                deleted.set(true);
            }

            return deleted.get();
        });
        Assertions.assertTrue(deleted.get());
        Log.info(this, instance.getName(), "application delete success");
    }

    @Test
    public void playSourceCodeApplication() {
        ApplicationInstanceSource codeSource = ApplicationInstanceSource.builder()
                .url("https://gitlab.com/yaolianhua/devops-jdk11.git")
                .startArgs("-Dspring.profiles.active=production")
                .startOptions("-Xms128m -Xmx512m")
                .gitBranch("master")
                .origin(ApplicationInstanceSource.Origin.SOURCE_CODE)
                .runtime(JavaRuntime.JAVA11)
                .build();
        ApplicationForm form = ApplicationForm.builder()
                .name("thymeleaf-fragments")
                .canHttp(true)
                .replicas(1)
                .serverPort(8080)
                .envs(Map.of())
                .source(codeSource)
                .build();

        ApplicationInstance one = applicationInstanceService.findActiveSucceed("admin", "thymeleaf-fragments");
        if (one != null) {
            player.delete(one.getId());
        }

        ApplicationInstance instance = player.play(form);

        AtomicBoolean succeed = new AtomicBoolean(false);

        await().atMost(20, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isSuccess()) {
                succeed.set(true);
            }

            return succeed.get();
        });

        Assertions.assertTrue(succeed.get());
        Log.info(this, instance.getName(), "application create success");


        player.delete(instance.getId());
        AtomicBoolean deleted = new AtomicBoolean(false);
        await().atMost(1, TimeUnit.MINUTES).until(() -> {
            ApplicationInstance fetched = applicationInstanceService.findOne(instance.getId());
            if (fetched.isDeleted()) {
                deleted.set(true);
            }

            return deleted.get();
        });
        Assertions.assertTrue(deleted.get());
        Log.info(this, instance.getName(), "application delete success");
    }

}
