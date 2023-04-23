package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.exception.NGCCAPlatformException;
import io.hotcloud.module.buildpack.BuildPack;
import io.hotcloud.module.buildpack.BuildPackService;
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
    @Deprecated(since = "BuildPackApiV2")
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
            throw new NGCCAPlatformException("Unsupported query condition", 400);
        }

        List<BuildPack> filtered = filter(buildPacks, done, deleted);
        return PageResult.ofCollectionPage(filtered, pageable);

    }

    /**
     * Paging query all {@link BuildPack} with giving parameter
     *
     * @param user     user's username
     * @param done     whether is done
     * @param deleted  whether is deleted
     * @param pageable {@link Pageable}
     * @return {@link BuildPack}
     */
    public PageResult<BuildPack> pagingQueryV2(@Nullable String user, @Nullable Boolean done, @Nullable Boolean deleted, Pageable pageable) {

        List<BuildPack> buildPacks;
        if (StringUtils.hasText(user)) {
            buildPacks = buildPackService.findAll(user);
        } else {
            buildPacks = buildPackService.findAll();
        }
        List<BuildPack> filtered = filter(buildPacks, done, deleted);
        return PageResult.ofCollectionPage(filtered, pageable);

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
