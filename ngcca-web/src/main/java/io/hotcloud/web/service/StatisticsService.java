package io.hotcloud.web.service;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.application.ApplicationInstanceStatistics;
import io.hotcloud.module.application.template.TemplateInstanceStatistics;
import io.hotcloud.module.buildpack.model.BuildPackStatistics;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.service.application.ApplicationInstanceStatisticsService;
import io.hotcloud.service.application.template.TemplateInstanceStatisticsService;
import io.hotcloud.service.buildpack.BuildPackStatisticsService;
import io.hotcloud.service.cluster.KubernetesClusterStatistics;
import io.hotcloud.service.cluster.KubernetesClusterStatisticsService;
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
    private final KubernetesClusterStatisticsService kubernetesClusterStatisticsService;


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
        BuildPackStatistics buildPackStatistics = buildPackStatisticsService.statistics(user.getUsername());
        ApplicationInstanceStatistics applicationInstanceStatistics = applicationInstanceStatisticsService.statistics(user.getUsername());
        KubernetesClusterStatistics kubernetesClusterStatistics = kubernetesClusterStatisticsService.statistics(user.getUsername());

        return Statistics.builder()
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
                .applications(applicationInstanceStatistics)
                .clusterStatistics(kubernetesClusterStatistics)
                .namespace(user.getNamespace())
                .user(user)
                .build();
    }

    /**
     * Get statistics
     *
     * @return {@link Statistics}
     */
    public Statistics statistics() {

        TemplateInstanceStatistics templateStatistics = templateInstanceStatisticsService.statistics("");
        BuildPackStatistics buildPackStatistics = buildPackStatisticsService.statistics("");
        ApplicationInstanceStatistics applicationInstanceStatistics = applicationInstanceStatisticsService.statistics("");
        KubernetesClusterStatistics kubernetesClusterStatistics = kubernetesClusterStatisticsService.statistics();

        return Statistics.builder()
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
                .applications(applicationInstanceStatistics)
                .clusterStatistics(kubernetesClusterStatistics)
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
