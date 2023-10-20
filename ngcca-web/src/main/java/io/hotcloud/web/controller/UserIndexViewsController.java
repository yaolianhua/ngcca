package io.hotcloud.web.controller;

import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.security.user.User;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.Log;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.Statistics;
import io.hotcloud.web.service.StatisticsService;
import io.hotcloud.web.views.UserViews;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class UserIndexViewsController {

    private final StatisticsService statisticsService;

    public UserIndexViewsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @RequestMapping(value = {"/index", "/"})
    @WebSession
    @Log(action = Action.QUERY, target = Target.DASHBOARD, activity = "访问用户首页")
    public String indexPage(Model model,
                            @CookieUser User user) {
        Statistics statistics = statisticsService.getUserStatisticsFromCache(user.getId());
        model.addAttribute(WebConstant.STATISTICS, statistics);
        return UserViews.INDEX;
    }
}
