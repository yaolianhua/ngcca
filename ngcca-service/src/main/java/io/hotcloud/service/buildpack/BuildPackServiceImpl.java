package io.hotcloud.service.buildpack;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.db.entity.BuildPackEntity;
import io.hotcloud.db.entity.BuildPackRepository;
import io.hotcloud.service.buildpack.model.BuildPack;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.hotcloud.service.buildpack.model.BuildPack.toBuildPack;

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

        entity.setJob(buildPack.getJobResource());
        entity.setSecret(buildPack.getSecretResource());

        if (StringUtils.hasText(entity.getId())) {
            entity.setModifiedAt(Instant.now());
            BuildPackEntity saveOrUpdate = buildPackRepository.save(entity);
            return toBuildPack(saveOrUpdate);
        }

        entity.setCreatedAt(Instant.now());
        BuildPackEntity saveOrUpdate = buildPackRepository.save(entity);

        return toBuildPack(saveOrUpdate);
    }

    @Override
    public List<BuildPack> findAll(String user) {
        List<BuildPackEntity> entities = buildPackRepository.findByUser(user);

        return entities.stream()
                .map(BuildPack::toBuildPack)
                .collect(Collectors.toList());
    }

    @Override
    public List<BuildPack> findAll() {
        Iterable<BuildPackEntity> all = buildPackRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .map(BuildPack::toBuildPack)
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
        entity.setModifiedAt(Instant.now());
        buildPackRepository.save(entity);

    }

}
