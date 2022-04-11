package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.model.*;
import io.hotcloud.common.Assert;
import io.hotcloud.common.HotCloudException;
import io.hotcloud.db.core.buildpack.BuildPackEntity;
import io.hotcloud.db.core.buildpack.BuildPackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
public class BuildPackServiceImpl implements BuildPackService {

    private final BuildPackRepository buildPackRepository;

    private final ObjectMapper objectMapper;

    public BuildPackServiceImpl(BuildPackRepository buildPackRepository,
                                ObjectMapper objectMapper) {
        this.buildPackRepository = buildPackRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public BuildPack saveOrUpdate(BuildPack buildPack) {

        Assert.notNull(buildPack, "BuildPack body is null", 400);
        Assert.hasText(buildPack.getUser(), "BuildPack user is null", 400);
        Assert.hasText(buildPack.getClonedId(), "BuildPack cloned id is null", 400);
        Assert.notNull(buildPack.getJobResource(), "BuildPack job body is null", 400);
        Assert.notNull(buildPack.getStorageResource(), "BuildPack storage body is null", 400);
        Assert.notNull(buildPack.getSecretResource(), "BuildPack secret body is null", 400);

        BuildPackEntity entity = (BuildPackEntity) new BuildPackEntity().copyToEntity(((DefaultBuildPack) buildPack));

        entity.setJob(writeJson(buildPack.getJobResource()));
        entity.setSecret(writeJson(buildPack.getSecretResource()));
        entity.setStorage(writeJson(buildPack.getStorageResource()));

        if (StringUtils.hasText(entity.getId())) {
            entity.setModifiedAt(LocalDateTime.now());
        }

        BuildPackEntity saveOrUpdate = buildPackRepository.save(entity);

        return toBuildPack(saveOrUpdate);
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
                .id(entity.getId())
                .jobResource(readT(entity.getJob(), BuildPackJobResource.class))
                .storageResource(readT(entity.getStorage(), BuildPackStorageResourceList.class))
                .secretResource(readT(entity.getSecret(), BuildPackDockerSecretResource.class))
                .yaml(entity.getYaml())
                .user(entity.getUser())
                .done(entity.isDone())
                .clonedId(entity.getClonedId())
                .message(entity.getMessage())
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
