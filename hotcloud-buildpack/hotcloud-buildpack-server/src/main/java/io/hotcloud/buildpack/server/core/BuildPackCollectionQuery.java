package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.model.BuildPack;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import io.hotcloud.common.exception.HotCloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class BuildPackCollectionQuery {

    private final BuildPackService buildPackService;

    public BuildPackCollectionQuery(BuildPackService buildPackService) {
        this.buildPackService = buildPackService;
    }

    /**
     * Paging query all {@link BuildPack} with giving parameter
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @param done     whether is done
     * @param deleted  whether is deleted
     * @param pageable {@link Pageable}
     * @return {@link BuildPack}
     */
    public PageResult<BuildPack> pagingQuery(@Nullable String user, @Nullable String clonedId, @Nullable Boolean done, @Nullable Boolean deleted, Pageable pageable) {

        List<BuildPack> buildPacks;
        if (StringUtils.hasText(user) && StringUtils.hasText(clonedId)) {
            buildPacks = buildPackService.findAll(user, clonedId);

        } else if (!StringUtils.hasText(user) && !StringUtils.hasText(clonedId)) {
            buildPacks = buildPackService.findAll();
        } else if (StringUtils.hasText(user)) {
            buildPacks = buildPackService.findAll(user);
        } else if (StringUtils.hasText(clonedId)) {
            buildPacks = buildPackService.findByClonedId(clonedId);
        } else {
            throw new HotCloudException("Unsupported query condition", 400);
        }

        List<BuildPack> filtered = filter(buildPacks, done, deleted);
        List<BuildPack> paging = PageResult.paging(filtered, pageable.getPage(), pageable.getPageSize());
        return PageResult.ofPage(paging, filtered.size(), pageable.getPage(), pageable.getPageSize());

    }

    public List<BuildPack> filter(List<BuildPack> data, Boolean done, Boolean deleted) {
        if (done == null && deleted == null) {
            return data;
        } else if (done != null && deleted != null) {
            return data.stream()
                    .filter(e -> Objects.equals(e.isDone(), done) &&
                            Objects.equals(e.isDeleted(), deleted))
                    .collect(Collectors.toList());
        } else if (done == null) {
            return data.stream()
                    .filter(e -> Objects.equals(e.isDeleted(), deleted))
                    .collect(Collectors.toList());
        }

        return data.stream()
                .filter(e -> Objects.equals(e.isDone(), done))
                .collect(Collectors.toList());
    }
}
