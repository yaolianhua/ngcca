package io.hotcloud.server.buildpack.service;

import io.hotcloud.vendor.buildpack.GitCloned;
import io.hotcloud.vendor.buildpack.GitClonedService;
import io.hotcloud.vendor.buildpack.GitClonedStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class GitClonedStatisticsService {

    private final GitClonedService gitClonedService;

    public GitClonedStatisticsService(GitClonedService gitClonedService) {
        this.gitClonedService = gitClonedService;
    }

    /**
     * Get GitClonedStatistics
     *
     * @param user user's username
     * @return {@link GitClonedStatistics}
     */
    public GitClonedStatistics statistics(@Nullable String user) {
        boolean hasUser = StringUtils.hasText(user);

        if (hasUser) {
            List<GitCloned> list = gitClonedService.findAll(user);
            return statistics(list);
        }

        List<GitCloned> list = gitClonedService.findAll();
        return statistics(list);
    }

    public GitClonedStatistics statistics(List<GitCloned> gitCloneds) {

        int success = (int) gitCloneds.stream()
                .filter(GitCloned::isSuccess)
                .count();

        int failed = (int) gitCloneds.stream()
                .filter(e -> !e.isSuccess())
                .count();

        int total = gitCloneds.size();

        return GitClonedStatistics.builder()
                .success(success)
                .failed(failed)
                .total(total)
                .build();
    }
}
