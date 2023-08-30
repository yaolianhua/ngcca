package io.hotcloud.server.db;

import io.hotcloud.db.entity.GitClonedEntity;
import io.hotcloud.db.entity.GitClonedRepository;
import io.hotcloud.server.ServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
@ActiveProfiles("test")
public class GitClonedRepositoryIT {

    @Autowired
    private GitClonedRepository gitClonedRepository;

    @After
    public void after() {
        gitClonedRepository.deleteAll();
    }

    @Test
    public void list() {
        GitClonedEntity entity = buildGitCloned("admin");
        gitClonedRepository.save(entity);

        List<GitClonedEntity> entities = gitClonedRepository.findByUser("guest");
        List<GitClonedEntity> adminEntities = gitClonedRepository.findByUser("admin");

        Assertions.assertTrue(entities.isEmpty());
        Assertions.assertEquals(1, adminEntities.size());

        List<GitClonedEntity> collect = StreamSupport.stream(gitClonedRepository.findAll().spliterator(), false).toList();
        Assertions.assertFalse(collect.isEmpty());
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
