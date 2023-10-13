package io.hotcloud.service.buildpack;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.service.buildpack.model.BuildPack;
import io.hotcloud.service.buildpack.model.BuildPackStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @param username username
     * @return {@link BuildPackStatistics}
     */
    public BuildPackStatistics userStatistics(String username) {
        if (!StringUtils.hasText(username)) {
            throw new PlatformException("username is missing");
        }

        List<BuildPack> buildPacks = buildPackService.findAll(username);
        return statistics(buildPacks);
    }

    public BuildPackStatistics allStatistics() {
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
