package io.hotcloud.buildpack.api;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.api.model.BuildPack;
import io.hotcloud.common.file.FileChangeWatcher;
import io.hotcloud.common.file.FileState;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.api.workload.JobApi;
import io.hotcloud.security.api.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class BuildPackPlayerIT extends BuildPackIntegrationTestBase {

    final CountDownLatch latch = new CountDownLatch(1);
    @Autowired
    private JobApi jobApi;
    @Autowired
    private PodApi podApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private KubectlApi kubectlApi;
    @Autowired
    private BuildPackPlayer buildPackPlayer;

    @Before
    public void before() {

        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Test
    public void buildpack_apply() throws IOException, InterruptedException {

//        String gitUrl = "https://gitlab.com/yaolianhua/hotcloud.git";
        String gitUrl = "https://gitee.com/yannanshan/devops-thymeleaf.git";
        BuildPack buildpack = buildPackPlayer.buildpack(gitUrl,
                "Dockerfile",
                true,
                true);

        Assertions.assertNotNull(buildpack);
        Assertions.assertTrue(StringUtils.hasText(buildpack.getBuildPackYaml()));

        log.info("BuildPack yaml \n {}", buildpack.getBuildPackYaml());
        String namespace = buildpack.getJob().getNamespace();
        String job = buildpack.getJob().getName();

        kubectlApi.apply(null, buildpack.getBuildPackYaml());

        Job jobRead = jobApi.read(namespace, job);
        Assertions.assertNotNull(jobRead);

        PodList podList = podApi.read(namespace, jobRead.getMetadata().getLabels());
        Assertions.assertEquals(1, podList.getItems().size());

        String clonedPath = buildpack.getRepository().getLocal();
        String tarball = buildpack.getJob().getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);
        FileState fileState = new FileState(Path.of(clonedPath, tarball));
        FileChangeWatcher fileChangeWatcher = new FileChangeWatcher(Path.of(clonedPath), event -> {
            if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                if (Objects.equals(event.context().toString(), tarball)) {
                    log.info("Git project '{}' image tar '{}' generated. size '{}'",
                            buildpack.getRepository().getProject(),
                            event.context().toString(),
                            Path.of(clonedPath, tarball).toFile().length());
                }
            }

            if (event.kind().name().equals(StandardWatchEventKinds.ENTRY_MODIFY.name())) {
                if (Objects.equals(event.context().toString(), tarball)) {
                    log.info("Git project '{}' image tar '{}' changed. size '{}'",
                            buildpack.getRepository().getProject(),
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
        while (latch.getCount() != 0) {
            TimeUnit.SECONDS.sleep(1);
            try {
                String logs = podApi.logs(namespace, pod.getMetadata().getName(), 1);
                System.out.print(logs);
            } catch (Exception e) {
//                log.warn("{}", e.getMessage());
            }
        }

        log.info("BuildPack done.");
        fileChangeWatcher.stop();
    }
}
