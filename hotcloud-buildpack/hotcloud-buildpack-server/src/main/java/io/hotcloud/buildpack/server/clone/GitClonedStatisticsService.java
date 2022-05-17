package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.api.clone.GitClonedStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
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
            List<GitCloned> list = gitClonedService.listCloned(user);
            return statistics(list);
        }

        List<GitCloned> list = gitClonedService.listAll();
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
