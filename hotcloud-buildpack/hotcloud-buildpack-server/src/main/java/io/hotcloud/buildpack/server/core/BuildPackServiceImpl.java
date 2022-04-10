package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.model.*;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.db.core.buildpack.BuildPackEntity;
import io.hotcloud.db.core.buildpack.BuildPackRepository;
import io.hotcloud.db.core.buildpack.GitClonedEntity;
import io.hotcloud.db.core.buildpack.GitClonedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class BuildPackServiceImpl implements BuildPackService {

    private final BuildPackRepository buildPackRepository;
    private final GitClonedRepository gitClonedRepository;

    private final ObjectMapper objectMapper;

    public BuildPackServiceImpl(BuildPackRepository buildPackRepository,
                                GitClonedRepository gitClonedRepository,
                                ObjectMapper objectMapper) {
        this.buildPackRepository = buildPackRepository;
        this.gitClonedRepository = gitClonedRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String clonedId, BuildPack buildPack) {

        Assert.notNull(buildPack, "BuildPack body is null", 400);
        Assert.notNull(buildPack.getJob(), "BuildPack job body is null", 400);
        Assert.notNull(buildPack.getStorage(), "BuildPack storage body is null", 400);
        Assert.notNull(buildPack.getDockerSecret(), "BuildPack secret body is null", 400);

        GitClonedEntity clonedEntity = gitClonedRepository.findById(clonedId).orElseThrow(() -> new HotCloudException("Cloned repository is not found [" + clonedId + "]"));

        Optional<BuildPackEntity> optionalBuildPack = buildPackRepository.findByUserAndClonedId(clonedEntity.getUser(), clonedId)
                .stream()
                .filter(e -> !e.isDone())
                .findFirst();

        Assert.state(optionalBuildPack.isEmpty(), String.format("[Conflict] '%s' user's git project '%s' is building",
                        clonedEntity.getUser(),
                        clonedEntity.getProject()),
                409);

        BuildPackEntity entity = new BuildPackEntity();
        entity.setUser(clonedEntity.getUser());
        entity.setClonedId(clonedId);
        entity.setJob(writeJson(buildPack.getJob()));
        entity.setSecret(writeJson(buildPack.getDockerSecret()));
        entity.setStorage(writeJson(buildPack.getStorage()));
        entity.setYaml(buildPack.getBuildPackYaml());

        buildPackRepository.save(entity);
    }

    @Override
    public List<BuildPack> findAll(String user, String clonedId) {
        List<BuildPackEntity> entities = buildPackRepository.findByUserAndClonedId(user, clonedId);

        return entities.stream()
                .map(this::toBuildPack)
                .collect(Collectors.toList());
    }

    @Override
    public BuildPack findOneWithNoDone(String user, String clonedId) {
        List<BuildPackEntity> entities = buildPackRepository.findByUserAndClonedId(user, clonedId);
        return entities.stream()
                .filter(e -> Objects.equals(e.isDone(), false))
                .map(this::toBuildPack)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteAll() {
        buildPackRepository.deleteAll();
    }

    private BuildPack toBuildPack(BuildPackEntity entity) {
        return DefaultBuildPack.builder()
                .job(readT(entity.getJob(), BuildPackJobResource.class))
                .storage(readT(entity.getStorage(), BuildPackStorageResourceList.class))
                .dockerSecret(readT(entity.getSecret(), BuildPackDockerSecretResource.class))
                .buildPackYaml(entity.getYaml())
                .build();
    }

    private <T> String writeJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new HotCloudException("Write value error. " + e.getCause().getMessage());
        }
    }

    private <T> T readT(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new HotCloudException("Read value error. " + e.getCause().getMessage());
        }
    }
}
