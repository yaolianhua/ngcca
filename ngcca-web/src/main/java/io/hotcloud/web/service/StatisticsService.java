package io.hotcloud.web.service;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.service.application.ApplicationInstanceStatisticsService;
import io.hotcloud.service.application.model.ApplicationInstanceStatistics;
import io.hotcloud.service.buildpack.BuildPackStatisticsService;
import io.hotcloud.service.buildpack.model.BuildPackStatistics;
import io.hotcloud.service.cluster.statistic.ClusterListStatistics;
import io.hotcloud.service.cluster.statistic.ClusterListStatisticsService;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.service.template.TemplateInstanceStatistics;
import io.hotcloud.service.template.TemplateInstanceStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final UserApi userApi;
    private final TemplateInstanceStatisticsService templateInstanceStatisticsService;
    private final BuildPackStatisticsService buildPackStatisticsService;
    private final ApplicationInstanceStatisticsService applicationInstanceStatisticsService;
    private final ClusterListStatisticsService clusterListStatisticsService;

    public static final String DASHBOARD_USER_STATISTICS_KEY = "dashboard:%s:statistics";
    public static final String DASHBOARD_ADMIN_STATISTICS_KEY = "dashboard:admin:statistics";
    private final Cache cache;

    public Statistics getUserStatisticsFromCache(String userid) {
        return cache.get(String.format(DASHBOARD_USER_STATISTICS_KEY, userid), () -> statistics(userid));
    }

    public Statistics getStatisticsFromCache() {
        return cache.get(DASHBOARD_ADMIN_STATISTICS_KEY, this::statistics);
    }

    /**
     * Get statistics with the giving {@code userid}
     *
     * @param userid user id
     * @return {@link Statistics}
     */
    public Statistics statistics(String userid) {
        Assert.hasText(userid, "user id is null");
        User user = userApi.find(userid);

        TemplateInstanceStatistics templateStatistics;
        BuildPackStatistics buildPackStatistics;
        ApplicationInstanceStatistics applicationInstanceStatistics;
        ClusterListStatistics clusterListStatistics;
        Statistics.StatisticsBuilder statisticsBuilder = Statistics.builder();

        templateStatistics = templateInstanceStatisticsService.userStatistics(user.getUsername());
        buildPackStatistics = buildPackStatisticsService.userStatistics(user.getUsername());
        applicationInstanceStatistics = applicationInstanceStatisticsService.userStatistics(user.getUsername());

        try {
            clusterListStatistics = clusterListStatisticsService.namespacedClusterListStatistics(user.getNamespace());
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get statistics error: " + e.getMessage());
            clusterListStatistics = new ClusterListStatistics();
        }
        return statisticsBuilder
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
                .applications(applicationInstanceStatistics)
                .clusterStatistics(clusterListStatistics)
                .namespace(null)
                .user(null)
                .build();
    }

    /**
     * Get statistics
     *
     * @return {@link Statistics}
     */
    public Statistics statistics() {

        TemplateInstanceStatistics templateStatistics;
        BuildPackStatistics buildPackStatistics;
        ApplicationInstanceStatistics applicationInstanceStatistics;
        ClusterListStatistics clusterListStatistics;
        Statistics.StatisticsBuilder statisticsBuilder = Statistics.builder();

        templateStatistics = templateInstanceStatisticsService.allStatistics();
        buildPackStatistics = buildPackStatisticsService.allStatistics();
        applicationInstanceStatistics = applicationInstanceStatisticsService.allStatistics();
        Collection<User> users = userApi.users();
        try {
            clusterListStatistics = clusterListStatisticsService.clusterListStatistics();
        } catch (Exception e) {
            Log.warn(this, null, Event.EXCEPTION, "get statistics error: " + e.getMessage());
            clusterListStatistics = new ClusterListStatistics();
        }
        return statisticsBuilder
                .users(users)
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
                .applications(applicationInstanceStatistics)
                .clusterStatistics(clusterListStatistics)
                .namespace(null)
                .user(null)
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
                .toList();
        return PageResult.ofCollectionPage(statistics, pageable);
    }

}
