package io.hotcloud.buildpack.server.git;

import io.hotcloud.buildpack.api.GitApi;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Validator;
import io.hotcloud.common.file.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class GitImpl implements GitApi {

    @Override
    public Boolean clone(String remote, String branch, String local, boolean force, @Nullable String username, @Nullable String password) {

        Assert.state(Validator.validHTTPGitAddress(remote), String.format("Invalid git url '%s', protocol supported only http(s)", remote), 400);

        if (force && FileHelper.exists(local)) {
            try {
                log.warn("The specified path '{}' is not empty, it will be forcibly deleted and then cloned", Path.of(local).toAbsolutePath());
                FileHelper.deleteRecursively(Path.of(local));
            } catch (IOException e) {
                log.error("Delete file path error: {} ", e.getCause().getMessage());
            }
        }
        Assert.state(!FileHelper.exists(local), String.format("Repository path '%s' already exist", local), 409);
        boolean needCredential = StringUtils.hasText(username) && StringUtils.hasText(password);

        log.info("Cloning from '{}' to '{}', branch [{}]", remote, Path.of(local).toAbsolutePath(), branch);
        final StopWatch watch = new StopWatch();
        watch.start();

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(remote)
                .setDirectory(Path.of(local).toFile())
                .setProgressMonitor(new SimpleProgressMonitor())
                .setTimeout(10);
        if (StringUtils.hasText(branch)) {
            cloneCommand.setBranch(branch);
        }
        if (needCredential) {
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
        }

        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
        try (Git result = cloneCommand.call()) {
            watch.stop();
            log.info("Cloned repository: '{}'. Takes '{}s'", result.getRepository().getDirectory(), ((int) watch.getTotalTimeSeconds()));
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Clone repository error. {}", e.getCause().getMessage());
            return Boolean.FALSE;
        }

    }
}
