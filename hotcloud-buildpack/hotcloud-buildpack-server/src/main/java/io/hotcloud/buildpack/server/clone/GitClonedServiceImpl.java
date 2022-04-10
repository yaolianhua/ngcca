package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitApi;
import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedEvent;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.common.Assert;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.db.core.buildpack.GitClonedEntity;
import io.hotcloud.db.core.buildpack.GitClonedRepository;
import io.hotcloud.security.api.UserApi;
import io.hotcloud.security.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class GitClonedServiceImpl implements GitClonedService {

    private final GitClonedRepository repository;

    private final Cache cache;
    private final GitApi gitApi;
    private final UserApi userApi;

    private final ExecutorService executorService;
    private final ApplicationEventPublisher eventPublisher;

    public GitClonedServiceImpl(GitClonedRepository repository,
                                Cache cache,
                                GitApi gitApi,
                                UserApi userApi,
                                ExecutorService executorService,
                                ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.cache = cache;
        this.gitApi = gitApi;
        this.userApi = userApi;
        this.executorService = executorService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void saveOrUpdate(GitCloned cloned) {
        Assert.notNull(cloned, "Git clone body is null", 400);
        Assert.hasText(cloned.getUrl(), "Git url is null", 400);
        Assert.hasText(cloned.getLocalPath(), "Git cloned path is null", 400);
        Assert.hasText(cloned.getProject(), "Git project is null", 400);

        GitClonedEntity existed = repository.findByUserAndProject(cloned.getUser(), cloned.getProject());
        if (existed != null) {
            existed.setError(cloned.getError());
            repository.save(existed);
            return;
        }

        GitClonedEntity entity = (GitClonedEntity) new GitClonedEntity().copyToEntity(cloned);
        repository.save(entity);
    }

    @Override
    public GitCloned findOne(String username, String gitProject) {
        Assert.hasText(username, "User's username is null", 400);
        Assert.hasText(gitProject, "Git project is null", 400);

        GitClonedEntity entity = repository.findByUserAndProject(username, gitProject);

        if (entity == null) {
            return null;
        }
        return entity.toT(GitCloned.class);
    }

    @Override
    public void deleteOne(String username, String gitProject) {
        GitClonedEntity one = repository.findByUserAndProject(username, gitProject);
        if (one == null) {
            log.debug("Git cloned record not found. user='{}', project='{}'", username, gitProject);
            return;
        }
        repository.delete(one);
    }

    @Override
    public void clone(String gitUrl, String branch, String username, String password) {

        Assert.hasText(gitUrl, "Git url is null", 400);

        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null", 404);

        //get user's namespace.
        String namespace = cache.get(String.format(UserApi.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null", 400);

        String gitProject = GitCloned.retrieveGitProject(gitUrl);
        String clonePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH, namespace, gitProject).toString();

        executorService.execute(() -> {
            GitCloned cloned = gitApi.clone(gitUrl, branch, clonePath, true, username, password);
            //The current user cannot be obtained from the asynchronous thread pool
            cloned.setUser(current.getUsername());
            cloned.setProject(gitProject);
            eventPublisher.publishEvent(new GitClonedEvent(cloned));
        });

    }
}
