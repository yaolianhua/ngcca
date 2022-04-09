package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.common.Assert;
import io.hotcloud.db.core.buildpack.GitClonedEntity;
import io.hotcloud.db.core.buildpack.GitClonedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class GitClonedServiceImpl implements GitClonedService {

    private final GitClonedRepository repository;

    public GitClonedServiceImpl(GitClonedRepository repository) {
        this.repository = repository;
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
}
