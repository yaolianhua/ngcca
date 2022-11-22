package io.hotcloud.buildpack.server.core;

import io.hotcloud.buildpack.api.core.BuildPack;
import io.hotcloud.buildpack.api.core.BuildPackService;
import io.hotcloud.buildpack.api.core.BuildPackStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.SUCCESS_MESSAGE;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class BuildPackStatisticsService {

    private final BuildPackService buildPackService;

    public BuildPackStatisticsService(BuildPackService buildPackService) {
        this.buildPackService = buildPackService;
    }

    /**
     * Get BuildPackStatistics
     *
     * @param user     user's username
     * @param clonedId git cloned id
     * @return {@link BuildPackStatistics}
     */
    public BuildPackStatistics statistics(@Nullable String user, @Nullable String clonedId) {
        boolean hasUser = StringUtils.hasText(user);
        boolean hasClonedId = StringUtils.hasText(clonedId);

        if (hasUser && hasClonedId) {
            List<BuildPack> buildPacks = buildPackService.findAll(user, clonedId);
            return statistics(buildPacks);
        }

        if (!hasUser && !hasClonedId) {
            List<BuildPack> buildPacks = buildPackService.findAll();
            return statistics(buildPacks);
        }

        if (hasUser) {
            List<BuildPack> buildPacks = buildPackService.findAll(user);
            return statistics(buildPacks);
        }

        List<BuildPack> buildPacks = buildPackService.findByClonedId(clonedId);
        return statistics(buildPacks);
    }

    public BuildPackStatistics statistics(List<BuildPack> buildPacks) {

        int deleted = ((int) buildPacks.stream().filter(BuildPack::isDeleted).count());
        int success = (int) buildPacks.stream().filter(e -> !e.isDeleted())
                .filter(BuildPack::isDone)
                .filter(e -> Objects.equals(SUCCESS_MESSAGE, e.getMessage()))
                .count();

        int failed = (int) buildPacks.stream().filter(e -> !e.isDeleted())
                .filter(BuildPack::isDone)
                .filter(e -> !Objects.equals(SUCCESS_MESSAGE, e.getMessage()))
                .count();

        int total = buildPacks.size();

        return BuildPackStatistics.builder()
                .deleted(deleted)
                .success(success)
                .failed(failed)
                .total(total)
                .build();
    }
}
