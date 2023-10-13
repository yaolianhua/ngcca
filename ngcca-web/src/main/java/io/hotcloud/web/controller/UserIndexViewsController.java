package io.hotcloud.web.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.activity.ALog;
import io.hotcloud.service.security.user.User;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.ActivityQuery;
import io.hotcloud.web.service.Statistics;
import io.hotcloud.web.service.StatisticsService;
import io.hotcloud.web.views.UserViews;
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
        Statistics statistics = statisticsService.userCachedStatistics(user.getId());
        PageResult<ALog> pageResult = activityQuery.pagingQuery(user.getUsername(), null, null, Pageable.of(1, 12));
        List<ALog> activities = pageResult.getData().stream().toList();
        model.addAttribute(WebConstant.STATISTICS, statistics);
        model.addAttribute(WebConstant.ACTIVITIES, activities);
        return UserViews.INDEX;
    }
}
