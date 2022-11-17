package io.hotcloud.buildpack.api.core;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.NgccaBuildPackApplication;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.common.api.core.files.FileChangeWatcher;
import io.hotcloud.common.api.core.files.FileState;
import io.hotcloud.kubernetes.client.http.JobClient;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.client.http.PodClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.security.api.user.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NgccaBuildPackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class BuildPackPlayerIT {

    @Autowired
    private JobClient jobApi;
    @Autowired
    private PodClient podApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private KubectlClient kubectlApi;
    @Autowired
    private NamespaceClient namespaceApi;

    @Autowired
    private AbstractBuildPackPlayer abstractBuildPackPlayer;

    @Autowired
    private GitClonedService gitClonedService;

    @Autowired
    private BuildPackService buildPackService;

    @Before
    public void before() {

        UserDetails adminUserDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken adminUsernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(adminUsernamePasswordAuthenticationToken);

    }

    private GitCloned cloned(String username, String url, String project) {
        gitClonedService.clone(url, null, null, null, null);

        gitClonedService.deleteOne(username, project);
        GitCloned cloned = null;
        while (null == cloned) {
            sleep(5);
            cloned = gitClonedService.findOne(username, project);
        }

        Assertions.assertFalse(StringUtils.hasText(cloned.getError()));

        return cloned;
    }

    @Test
    public void apply_multi() {
        buildPackService.deleteAll();
        CountDownLatch latchMulti = new CountDownLatch(3);
        new Thread(() -> {
            try {
                single("admin", "https://gitee.com/yannanshan/devops-thymeleaf.git", "devops-thymeleaf");
                latchMulti.countDown();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }, "admin").start();
        new Thread(() -> {
            try {
                single("guest", "https://gitee.com/yannanshan/devops-thymeleaf.git", "devops-thymeleaf");
                latchMulti.countDown();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }, "guest").start();
        new Thread(() -> {
            try {
                single("clientuser", "https://gitee.com/yannanshan/devops-thymeleaf.git", "devops-thymeleaf");
                latchMulti.countDown();
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }, "clientuser").start();

        while (true) {
            sleep(10);
            if (latchMulti.getCount() == 0) {
                log.info("All user's buildPack done!");
                break;
            }
            log.info("Not all done yet!");
        }
        buildPackService.deleteAll();
    }

    private void single(String username, String url, String project) throws ApiException {
        final CountDownLatch latch = new CountDownLatch(1);
        UserDetails userDetails = userApi.retrieve(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        GitCloned gitCloned = gitClonedService.findOne(username, project);
        if (null == gitCloned) {
            gitCloned = cloned(username, url, project);
        }
        Assertions.assertNotNull(gitCloned);

        BuildPack buildPack = abstractBuildPackPlayer.apply(gitCloned.getId(), null);

        new Thread(() -> {
            while (true) {
                sleep(5);
                try {
                    BuildPack find = buildPackService.findOne(buildPack.getId());
                    if (find.isDone()) {
                        if (Objects.equals("success", find.getMessage()) && StringUtils.hasText(find.getArtifact())) {
                            latch.countDown();
                            break;
                        }
                        if (StringUtils.hasText(find.getMessage()) && !"success".equals(find.getMessage())) {
                            latch.countDown();
                            break;
                        }
                    }
                } catch (Exception e) {
                    //why NPE?
                    log.error("{}", e.getCause().getMessage(), e);
                }

            }
        }, "buildpack").start();

        while (true) {
            if (latch.getCount() == 0) {
                BuildPack find = buildPackService.findOne(buildPack.getId());
                log.info("{} user's buildPack [{}] done! \n message: '{}' \n logs: \n {} artifact url: {}", username, find.getId(), find.getMessage(), find.getLogs(), find.getArtifact());
                jobApi.delete(find.getJobResource().getNamespace(), find.getJobResource().getName());
                break;
            }
        }
    }

    @Test
    public void apply_single() throws ApiException {
        buildPackService.deleteAll();
        single("admin", "https://gitee.com/yannanshan/devops-thymeleaf.git", "devops-thymeleaf");
        buildPackService.deleteAll();
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void buildPack_apply_manually() throws IOException, ApiException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        String gitUrl = "https://gitee.com/yannanshan/devops-thymeleaf.git";

        gitClonedService.clone(gitUrl, null, null, null, null);
        gitClonedService.deleteOne("admin", "devops-thymeleaf");
        GitCloned cloned = null;
        while (null == cloned) {
            TimeUnit.SECONDS.sleep(5);
            cloned = gitClonedService.findOne("admin", "devops-thymeleaf");
        }

        Assertions.assertFalse(StringUtils.hasText(cloned.getError()));
        BuildPack buildpack = abstractBuildPackPlayer.buildpack(cloned.getId(), true);

        Assertions.assertNotNull(buildpack);
        Assertions.assertTrue(StringUtils.hasText(buildpack.getYaml()));

        log.info("BuildPack yaml \n {}", buildpack.getYaml());
        String namespace = buildpack.getJobResource().getNamespace();
        namespaceApi.create(namespace);

        String job = buildpack.getJobResource().getName();

        kubectlApi.resourceListCreateOrReplace(null, YamlBody.of(buildpack.getYaml()));

        Job jobRead = jobApi.read(namespace, job);
        Assertions.assertNotNull(jobRead);

        PodList podList = podApi.readList(namespace, jobRead.getMetadata().getLabels());
        Assertions.assertEquals(1, podList.getItems().size());

        String clonedPath = buildpack.getJobResource().getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
        String gitProject = buildpack.getJobResource().getAlternative().get(BuildPackConstant.GIT_PROJECT_NAME);
        String tarball = buildpack.getJobResource().getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

        FileState fileState = new FileState(Path.of(clonedPath, tarball));
        FileChangeWatcher fileChangeWatcher = new FileChangeWatcher(Path.of(clonedPath), event -> {
            if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                if (Objects.equals(event.context().toString(), tarball)) {
                    log.info("Git project '{}' image tar '{}' generated. size '{}'",
                            gitProject,
                            event.context().toString(),
                            Path.of(clonedPath, tarball).toFile().length());
                }
            }

            if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                if (Objects.equals(event.context().toString(), tarball)) {
                    log.info("Git project '{}' image tar '{}' changed. size '{}'",
                            gitProject,
                            event.context().toString(),
                            Path.of(clonedPath, tarball).toFile().length());
                }
            }

        });
        fileChangeWatcher.start();

        new Thread(() -> {
            while (true) {
                boolean waitCompleted = fileState.waitCompleted();
                if (waitCompleted) {
                    latch.countDown();
                    break;
                }
            }
        }, "file-state").start();
        Pod pod = podList.getItems().get(0);
        String line = "";
        while (latch.getCount() != 0) {

            try {
                String logs = podApi.logs(namespace, pod.getMetadata().getName(), 1);
                if (!Objects.equals(line, logs)) {
                    System.out.print(logs);
                }
                line = logs;
            } catch (Exception e) {
//                log.warn("{}", e.getMessage());
            }
        }

        log.info("BuildPack done.");
        fileChangeWatcher.stop();
    }
}
