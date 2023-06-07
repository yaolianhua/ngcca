package io.hotcloud.service.buildpack;

import io.hotcloud.common.file.FileHelper;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.utils.Validator;
import io.hotcloud.module.buildpack.GitApi;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class GitImpl implements GitApi {

    @Override
    public void clone(String remote, String branch, String local, boolean force, @Nullable String username, @Nullable String password) {

        Assert.state(Validator.validHTTPGitAddress(remote), String.format("Invalid git url '%s', protocol supported only http(s)", remote));

        if (force && FileHelper.exists(local)) {
            try {
                Log.warn(this, null,
                        String.format("The specified path '%s' is not empty, it will be forcibly deleted and then cloned", Path.of(local).toAbsolutePath()));
                FileHelper.deleteRecursively(Path.of(local));
            } catch (IOException e) {
                Log.error(this, null,
                        String.format("Delete file path error: %s", e.getCause().getMessage()));
                return;
            }
        }
        Assert.state(!FileHelper.exists(local), String.format("Repository path '%s' already exist", local));
        boolean needCredential = StringUtils.hasText(username) && StringUtils.hasText(password);

        Log.info(this, null,
                String.format("Cloning from '%s' to '%s', branch [%s]", remote, Path.of(local).toAbsolutePath(), branch));
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
            Log.info(this, null,
                    String.format("Cloned repository: '%s'. Takes '%ss'", result.getRepository().getDirectory(), ((int) watch.getTotalTimeSeconds())));
        } catch (Exception e) {
            Log.error(this, null,
                    String.format("Clone repository error. %s", e.getCause().getMessage()));

        }

    }
}
