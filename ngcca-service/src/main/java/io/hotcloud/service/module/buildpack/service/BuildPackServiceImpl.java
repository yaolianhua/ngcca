package io.hotcloud.service.module.buildpack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.buildpack.model.BuildPackDockerSecretResource;
import io.hotcloud.module.buildpack.model.BuildPackJobResource;
import io.hotcloud.module.db.entity.BuildPackEntity;
import io.hotcloud.module.db.entity.BuildPackRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
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
        Assert.notNull(buildPack.getJobResource(), "BuildPack job body is null");
        Assert.notNull(buildPack.getSecretResource(), "BuildPack secret body is null");

        BuildPackEntity entity = (BuildPackEntity) new BuildPackEntity().toE(buildPack);

        entity.setJob(writeJson(buildPack.getJobResource()));
        entity.setSecret(writeJson(buildPack.getSecretResource()));

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
    public List<BuildPack> findAll(String user) {
        List<BuildPackEntity> entities = buildPackRepository.findByUser(user);

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
    public BuildPack findByUuid(String uuid) {
        BuildPackEntity entity = buildPackRepository.findByUuid(uuid);
        return entity == null ? null : toBuildPack(entity);
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
                .uuid(entity.getUuid())
                .jobResource(readT(entity.getJob(), BuildPackJobResource.class))
                .secretResource(readT(entity.getSecret(), BuildPackDockerSecretResource.class))
                .yaml(entity.getYaml())
                .user(entity.getUser())
                .done(entity.isDone())
                .deleted(entity.isDeleted())
                .httpGitUrl(entity.getHttpGitUrl())
                .gitBranch(entity.getGitBranch())
                .message(entity.getMessage())
                .logs(entity.getLogs())
                .artifact(entity.getArtifact())
                .packageUrl(entity.getPackageUrl())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }

    private <T> String writeJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new PlatformException("Write value error. " + e.getCause().getMessage());
        }
    }

    private <T> T readT(String content, Class<T> clazz) {
        try {
            return objectMapper.readValue(content, clazz);
        } catch (JsonProcessingException e) {
            throw new PlatformException("Read value error. " + e.getCause().getMessage());
        }
    }
}
