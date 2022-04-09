package io.hotcloud.db.api.buildpack;

import io.hotcloud.db.DatabaseIntegrationTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
public class GitClonedRepositoryIT extends DatabaseIntegrationTestBase {

    @Autowired
    private GitClonedRepository gitClonedRepository;

    @After
    public void after() {
        gitClonedRepository.deleteAll();
    }

    @Test
    public void readByUserAndProject() {
        GitClonedEntity gitCloned = buildGitCloned("admin");
        gitClonedRepository.save(gitCloned);

        GitClonedEntity entity = gitClonedRepository.findByUserAndProject("admin", "devops-thymeleaf");
        Assertions.assertNotNull(entity);

        gitClonedRepository.delete(entity);

    }

    @Test
    public void update() throws InterruptedException {
        GitClonedEntity gitCloned = buildGitCloned("admin");
        gitClonedRepository.save(gitCloned);

        GitClonedEntity entity = gitClonedRepository.findByUserAndProject("admin", "devops-thymeleaf");
        entity.setSuccess(false);
        entity.setError("error!");
        TimeUnit.MILLISECONDS.sleep(500);
        entity.setModifiedAt(LocalDateTime.now());

        gitClonedRepository.save(entity);
        GitClonedEntity updated = gitClonedRepository.findByUserAndProject("admin", "devops-thymeleaf");

        Assertions.assertFalse(updated.isSuccess());
        Assertions.assertNotEquals(entity.getModifiedAt().getNano(), updated.getModifiedAt().getNano());
        Assertions.assertEquals(entity.getCreatedAt(), updated.getCreatedAt());

        gitClonedRepository.delete(entity);
    }


    private GitClonedEntity buildGitCloned(String user) {
        GitClonedEntity gitCloned = new GitClonedEntity();
        gitCloned.setUser(user);
        gitCloned.setLocalPath("/tmp/kaniko/6f83d4d1c8ad40fdaa4bd9649088a9d8/devops-thymeleaf");
        gitCloned.setUrl("https://gitee.com/yannanshan/devops-thymeleaf.git");
        gitCloned.setProject("devops-thymeleaf");

        return gitCloned;
    }
}
