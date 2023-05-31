package io.hotcloud.web.user;

import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.security.user.User;
import io.hotcloud.web.Views;
import io.hotcloud.web.activity.ActivityQuery;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.statistics.Statistics;
import io.hotcloud.web.statistics.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping
public class UserIndexViewsController {

    private final StatisticsService statisticsService;
    private final ActivityQuery activityQuery;

    public UserIndexViewsController(StatisticsService statisticsService, ActivityQuery activityQuery) {
        this.statisticsService = statisticsService;
        this.activityQuery = activityQuery;
    }

    @RequestMapping(value = {"/index", "/"})
    @WebSession
    public String indexPage(Model model,
                            @CookieUser User user) {
        Statistics statistics = statisticsService.statistics(user.getId());
        PageResult<ActivityLog> pageResult = activityQuery.pagingQuery(user.getUsername(), null, null, Pageable.of(1, 8));
        List<ActivityLog> activities = pageResult.getData().stream().toList();
        model.addAttribute(WebConstant.STATISTICS, statistics);
        model.addAttribute(WebConstant.ACTIVITIES, activities);
        return Views.INDEX;
    }
}
