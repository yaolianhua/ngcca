package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.*;
import io.hotcloud.buildpack.api.core.BuildPackConstant;
import io.hotcloud.common.Validator;
import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.exception.HotCloudException;
import io.hotcloud.db.core.buildpack.GitClonedEntity;
import io.hotcloud.db.core.buildpack.GitClonedRepository;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.hotcloud.security.api.SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX;

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
    public GitCloned saveOrUpdate(GitCloned cloned) {
        Assert.notNull(cloned, "Git clone body is null");
        Assert.hasText(cloned.getUrl(), "Git url is null");
        Assert.hasText(cloned.getLocalPath(), "Git cloned path is null");
        Assert.hasText(cloned.getProject(), "Git project is null");

        GitClonedEntity existed = repository.findByUserAndProject(cloned.getUser(), cloned.getProject());
        if (existed != null) {
            existed.setError(cloned.getError());
            existed.setModifiedAt(LocalDateTime.now());
            GitClonedEntity updated = repository.save(existed);
            return updated.toT(GitCloned.class);
        }

        GitClonedEntity entity = (GitClonedEntity) new GitClonedEntity().copyToEntity(cloned);
        entity.setCreatedAt(LocalDateTime.now());
        GitClonedEntity saved = repository.save(entity);

        return saved.toT(GitCloned.class);
    }

    @Override
    public GitCloned findOne(String user, String gitProject) {
        Assert.hasText(user, "User's username is null");
        Assert.hasText(gitProject, "Git project is null");

        GitClonedEntity entity = repository.findByUserAndProject(user, gitProject);

        if (entity == null) {
            return null;
        }
        return entity.toT(GitCloned.class);
    }

    @Override
    public GitCloned findOne(String clonedId) {
        Optional<GitClonedEntity> optionalGitCloned = repository.findById(clonedId);
        GitClonedEntity entity = optionalGitCloned.orElse(null);

        return entity == null ? null : entity.toT(GitCloned.class);
    }

    @Override
    public List<GitCloned> listCloned(String user) {
        return repository.findByUser(user)
                .stream()
                .map(e -> e.toT(GitCloned.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GitCloned> listAll() {
        Iterable<GitClonedEntity> all = repository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(e -> e.toT(GitCloned.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOne(String user, String gitProject) {
        GitClonedEntity one = repository.findByUserAndProject(user, gitProject);
        if (one == null) {
            log.debug("Git cloned record not found. user='{}', project='{}'", user, gitProject);
            return;
        }
        repository.delete(one);
    }

    @Override
    public void delete(String user) {
        List<GitClonedEntity> clonedEntities = repository.findByUser(user);
        repository.deleteAll(clonedEntities);
    }

    @Override
    public void deleteById(String id) {

        GitClonedEntity one = repository.findById(id).orElseThrow(() -> new HotCloudException("Git repository not found [" + id + "]"));
        repository.delete(one);

        GitCloned cloned = one.toT(GitCloned.class);
        eventPublisher.publishEvent(new GitClonedDeleteEvent(cloned));
    }

    @Override
    public void clone(String gitUrl, String dockerfile, String branch, String username, String password) {

        Assert.hasText(gitUrl, "Git url is null");

        User current = userApi.current();
        Assert.notNull(current, "Retrieve current user null");

        //get user's namespace.
        String namespace = cache.get(String.format(CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "namespace is null");

        Assert.state(Validator.validHTTPGitAddress(gitUrl), "http(s) git url support only");
        String gitProject = GitCloned.retrieveGitProject(gitUrl);
        String clonePath = Path.of(BuildPackConstant.STORAGE_VOLUME_PATH, namespace, gitProject).toString();

        executorService.execute(() -> {
            GitCloned cloned = gitApi.clone(gitUrl, branch, clonePath, true, username, password);
            //The current user cannot be obtained from the asynchronous thread pool
            cloned.setUser(current.getUsername());
            cloned.setProject(gitProject);
            cloned.setDockerfile(StringUtils.hasText(dockerfile) ? dockerfile : "Dockerfile");
            eventPublisher.publishEvent(new GitClonedCreateEvent(cloned));
        });

    }
}
