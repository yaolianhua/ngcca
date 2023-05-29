package io.hotcloud.service.module.buildpack.service;

import io.hotcloud.module.buildpack.BuildPackService;
import io.hotcloud.module.buildpack.model.BuildPack;
import io.hotcloud.module.buildpack.model.BuildPackStatistics;
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
     * @param user user's username
     * @return {@link BuildPackStatistics}
     */
    public BuildPackStatistics statistics(@Nullable String user) {
        boolean hasUser = StringUtils.hasText(user);

        if (hasUser) {
            List<BuildPack> buildPacks = buildPackService.findAll(user);
            return statistics(buildPacks);
        }

        List<BuildPack> buildPacks = buildPackService.findAll();
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
