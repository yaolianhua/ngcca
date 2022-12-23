package io.hotcloud.allinone.web.statistics;

import io.hotcloud.application.api.core.ApplicationInstanceStatistics;
import io.hotcloud.application.api.template.TemplateInstanceStatistics;
import io.hotcloud.application.server.core.ApplicationInstanceStatisticsService;
import io.hotcloud.application.server.template.TemplateInstanceStatisticsService;
import io.hotcloud.buildpack.api.clone.GitClonedStatistics;
import io.hotcloud.buildpack.api.core.BuildPackStatistics;
import io.hotcloud.buildpack.server.clone.GitClonedStatisticsService;
import io.hotcloud.buildpack.server.core.BuildPackStatisticsService;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final UserApi userApi;
    private final TemplateInstanceStatisticsService templateInstanceStatisticsService;
    private final GitClonedStatisticsService gitClonedStatisticsService;
    private final BuildPackStatisticsService buildPackStatisticsService;
    private final ApplicationInstanceStatisticsService applicationInstanceStatisticsService;


    /**
     * Get statistics with the giving {@code userid}
     *
     * @param userid user id
     * @return {@link Statistics}
     */
    public Statistics statistics(String userid) {
        Assert.hasText(userid, "user id is null");
        User user = userApi.find(userid);

        TemplateInstanceStatistics templateStatistics = templateInstanceStatisticsService.statistics(user.getUsername());
        GitClonedStatistics clonedStatistics = gitClonedStatisticsService.statistics(user.getUsername());
        BuildPackStatistics buildPackStatistics = buildPackStatisticsService.statistics(user.getUsername(), null);
        final ApplicationInstanceStatistics applicationInstanceStatistics = applicationInstanceStatisticsService.statistics(user.getUsername());

        return Statistics.builder()
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
                .repositories(clonedStatistics)
                .applications(applicationInstanceStatistics)
                .namespace(user.getNamespace())
                .user(user)
                .build();
    }

    /**
     * Get statistics with all users
     *
     * @param pageable {@link  Pageable}
     * @return paged statistics
     */
    public PageResult<Statistics> statistics(Pageable pageable) {
        Collection<User> users = userApi.users();

        List<Statistics> statistics = users.stream()
                .map(User::getId)
                .map(this::statistics)
                .collect(Collectors.toList());
        return PageResult.ofCollectionPage(statistics, pageable);
    }

}
