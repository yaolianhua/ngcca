package io.hotcloud.buildpack.api.core;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.hotcloud.buildpack.BuildPackIntegrationTestBase;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.common.file.FileChangeWatcher;
import io.hotcloud.common.file.FileState;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.kubernetes.api.pod.PodApi;
import io.hotcloud.kubernetes.api.workload.JobApi;
import io.hotcloud.security.api.UserApi;
import io.kubernetes.client.openapi.ApiException;
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
    private NamespaceApi namespaceApi;

    @Autowired
    private AbstractBuildPackPlayer abstractBuildPackPlayer;

    @Autowired
    private GitClonedService gitClonedService;

    @Before
    public void before() {

        UserDetails userDetails = userApi.retrieve("admin");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

    @Test
    public void buildPack_apply_manually() throws IOException, ApiException, InterruptedException {

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
        Assertions.assertTrue(StringUtils.hasText(buildpack.getBuildPackYaml()));

        log.info("BuildPack yaml \n {}", buildpack.getBuildPackYaml());
        String namespace = buildpack.getJob().getNamespace();
        namespaceApi.namespace(namespace);

        String job = buildpack.getJob().getName();

        kubectlApi.apply(null, buildpack.getBuildPackYaml());

        Job jobRead = jobApi.read(namespace, job);
        Assertions.assertNotNull(jobRead);

        PodList podList = podApi.read(namespace, jobRead.getMetadata().getLabels());
        Assertions.assertEquals(1, podList.getItems().size());

        String clonedPath = buildpack.getJob().getAlternative().get(BuildPackConstant.GIT_PROJECT_PATH);
        String gitProject = buildpack.getJob().getAlternative().get(BuildPackConstant.GIT_PROJECT_NAME);
        String tarball = buildpack.getJob().getAlternative().get(BuildPackConstant.GIT_PROJECT_TARBALL);

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
