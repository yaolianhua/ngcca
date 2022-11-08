package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitApi;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.Validator;
import io.hotcloud.common.api.core.files.FileHelper;
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

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class GitImpl implements GitApi {

    private GitCloned build(String remote, String branch, String local, boolean force, String username, String password, boolean success, String error) {
        return GitCloned.builder()
                .success(success)
                .url(remote)
                .branch(branch)
                .localPath(local)
                .force(force)
                .username(username)
                .password(password)
                .error(error)
                .build();
    }

    @Override
    public GitCloned clone(String remote, String branch, String local, boolean force, @Nullable String username, @Nullable String password) {

        Assert.state(Validator.validHTTPGitAddress(remote), String.format("Invalid git url '%s', protocol supported only http(s)", remote));

        if (force && FileHelper.exists(local)) {
            try {
                Log.warn(GitImpl.class.getName(),
                        String.format("The specified path '%s' is not empty, it will be forcibly deleted and then cloned", Path.of(local).toAbsolutePath()));
                FileHelper.deleteRecursively(Path.of(local));
            } catch (IOException e) {
                Log.error(GitImpl.class.getName(),
                        String.format("Delete file path error: %s", e.getCause().getMessage()));
                return build(remote, branch, local, force, username, password, false, e.getCause().getMessage());
            }
        }
        Assert.state(!FileHelper.exists(local), String.format("Repository path '%s' already exist", local));
        boolean needCredential = StringUtils.hasText(username) && StringUtils.hasText(password);

        Log.info(GitImpl.class.getName(),
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
            Log.info(GitImpl.class.getName(),
                    String.format("Cloned repository: '%s'. Takes '%ss'", result.getRepository().getDirectory(), ((int) watch.getTotalTimeSeconds())));
            return build(remote, branch, local, force, username, password, true, null);
        } catch (Exception e) {
            Log.error(GitImpl.class.getName(),
                    String.format("Clone repository error. %s", e.getCause().getMessage()));
            return build(remote, branch, local, force, username, password, false, e.getCause().getMessage());
        }

    }
}
