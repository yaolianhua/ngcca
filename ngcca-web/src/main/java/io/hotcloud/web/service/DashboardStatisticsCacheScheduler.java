package io.hotcloud.web.service;

import io.hotcloud.common.cache.Cache;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.service.security.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardStatisticsCacheScheduler {

    private final StatisticsService statisticsService;
    private final UserApi userApi;
    private final Cache cache;

    @Scheduled(cron = "*/8 * * * * *")
    public void refresh() {

        Log.debug(this, null, Event.SCHEDULE, "dashboard statistics cache refresh task is running");
        //
        userApi.users().forEach(u -> {
            try {
                Statistics statistics = statisticsService.userStatistics(u.getId());
                cache.put(String.format(StatisticsService.DASHBOARD_USER_STATISTICS_KEY, u.getId()), statistics);
            } catch (Exception e) {
                Log.error(this, null, Event.EXCEPTION, "refresh user [" + u.getId() + "] statistics cache error: " + e.getMessage());
            }
        });


        try {
            Statistics statistics = statisticsService.allStatistics();
            cache.put(StatisticsService.DASHBOARD_ADMIN_STATISTICS_KEY, statistics);
        } catch (Exception e) {
            Log.error(this, null, Event.SCHEDULE, "refresh admin statistics cache error: " + e.getMessage());
        }

    }
}
