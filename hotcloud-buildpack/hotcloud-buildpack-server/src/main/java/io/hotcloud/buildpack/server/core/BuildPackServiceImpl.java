package io.hotcloud.buildpack.server.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.buildpack.api.core.*;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.db.core.buildpack.BuildPackEntity;
import io.hotcloud.db.core.buildpack.BuildPackRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        Assert.notNull(buildPack, "BuildPack body is null");
        Assert.hasText(buildPack.getUser(), "BuildPack user is null");
        Assert.hasText(buildPack.getClonedId(), "BuildPack cloned id is null");
        Assert.notNull(buildPack.getJobResource(), "BuildPack job body is null");
        Assert.notNull(buildPack.getStorageResource(), "BuildPack storage body is null");
        Assert.notNull(buildPack.getSecretResource(), "BuildPack secret body is null");

        BuildPackEntity entity = (BuildPackEntity) new BuildPackEntity().copyToEntity(buildPack);

        entity.setJob(writeJson(buildPack.getJobResource()));
        entity.setSecret(writeJson(buildPack.getSecretResource()));
        entity.setStorage(writeJson(buildPack.getStorageResource()));

        if (StringUtils.hasText(entity.getId())) {
            entity.setModifiedAt(LocalDateTime.now());
            BuildPackEntity saveOrUpdate = buildPackRepository.save(entity);
            return toBuildPack(saveOrUpdate);
        }

        entity.setCreatedAt(LocalDateTime.now());
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
    public List<BuildPack> findAll(String user) {
        List<BuildPackEntity> entities = buildPackRepository.findByUser(user);

        return entities.stream()
                .map(this::toBuildPack)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildPack> findByClonedId(String clonedId) {
        List<BuildPackEntity> entities = buildPackRepository.findByClonedId(clonedId);
        return entities.stream()
                .map(this::toBuildPack)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildPack> findAll() {
        Iterable<BuildPackEntity> all = buildPackRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(this::toBuildPack)
                .collect(Collectors.toList());
    }

    @Override
    public BuildPack findOne(String id) {
        BuildPackEntity entity = buildPackRepository.findById(id).orElse(null);
        return entity == null ? null : toBuildPack(entity);
    }

    @Override
    public BuildPack findOneOrNullWithNoDone(String user, String clonedId) {
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

    @Override
    public void deleteAll(String user) {
        List<BuildPackEntity> entities = buildPackRepository.findByUser(user);
        buildPackRepository.deleteAll(entities);
    }

    @Override
    public void delete(String id, boolean physically) {
        if (!StringUtils.hasText(id)) {
            return;
        }
        if (physically) {
            buildPackRepository.deleteById(id);
            return;
        }

        BuildPackEntity entity = buildPackRepository.findById(id).orElse(null);
        if (entity == null) {
            return;
        }
        entity.setDeleted(true);
        entity.setModifiedAt(LocalDateTime.now());
        buildPackRepository.save(entity);

    }

    private BuildPack toBuildPack(BuildPackEntity entity) {
        return BuildPack.builder()
                .id(entity.getId())
                .jobResource(readT(entity.getJob(), BuildPackJobResource.class))
                .storageResource(readT(entity.getStorage(), BuildPackStorageResourceList.class))
                .secretResource(readT(entity.getSecret(), BuildPackDockerSecretResource.class))
                .yaml(entity.getYaml())
                .user(entity.getUser())
                .done(entity.isDone())
                .deleted(entity.isDeleted())
                .clonedId(entity.getClonedId())
                .message(entity.getMessage())
                .logs(entity.getLogs())
                .artifact(entity.getArtifact())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
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
