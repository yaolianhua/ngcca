package io.hotcloud.buildpack.server.git;

import io.hotcloud.buildpack.api.GitApi;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Validator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class GitImpl implements GitApi {

    @Override
    public Boolean clone(String remote, String branch, String local, @Nullable String username, @Nullable String password) {

        Assert.state(!Validator.existedPath(local), String.format("Repository path '%s' already exist", local), 409);
        Assert.state(Validator.validHTTPSGitAddress(remote), String.format("Invalid git url '%s', protocol supported only https", remote), 400);

        log.info("Cloning from '{}' to '{}', branch [{}]", remote, local, branch);
        final StopWatch watch = new StopWatch();
        watch.start();
        if (StringUtils.hasText(branch)) {
            try (Git result = Git.cloneRepository()
                    .setURI(remote)
                    .setBranch(branch)
                    .setDirectory(Path.of(local).toFile())
                    .setProgressMonitor(new SimpleProgressMonitor())
                    .setTimeout(5)
                    .call()) {

                watch.stop();
                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                log.info("Cloned repository: '{}'. Takes '{}s'", result.getRepository().getDirectory(), ((int) watch.getTotalTimeSeconds()));
                return Boolean.TRUE;
            } catch (GitAPIException e) {
                log.error("Clone repository error. {}", e.getMessage(), e);
                return Boolean.FALSE;
            }
        }

        try (Git result = Git.cloneRepository()
                .setURI(remote)
                .setDirectory(Path.of(local).toFile())
                .setProgressMonitor(new SimpleProgressMonitor())
                .setTimeout(5)
                .call()) {

            watch.stop();
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            log.info("Cloned repository: '{}'. Takes '{}s'", result.getRepository().getDirectory(), ((int) watch.getTotalTimeSeconds()));
            return Boolean.TRUE;
        } catch (GitAPIException e) {
            log.error("Clone repository error. {}", e.getMessage(), e);
        }

        return Boolean.FALSE;
    }
}
