package io.hotcloud.web.statistics;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.application.core.ApplicationInstanceStatistics;
import io.hotcloud.module.application.template.TemplateInstanceStatistics;
import io.hotcloud.module.buildpack.model.BuildPackStatistics;
import io.hotcloud.module.security.user.User;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.service.module.application.core.ApplicationInstanceStatisticsService;
import io.hotcloud.service.module.application.template.TemplateInstanceStatisticsService;
import io.hotcloud.service.module.buildpack.service.BuildPackStatisticsService;
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
        BuildPackStatistics buildPackStatistics = buildPackStatisticsService.statistics(user.getUsername());
        final ApplicationInstanceStatistics applicationInstanceStatistics = applicationInstanceStatisticsService.statistics(user.getUsername());

        return Statistics.builder()
                .buildPacks(buildPackStatistics)
                .templates(templateStatistics)
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
