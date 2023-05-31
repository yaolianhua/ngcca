package io.hotcloud.web.user;

import io.hotcloud.common.model.ActivityLog;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.security.user.User;
import io.hotcloud.web.Views;
import io.hotcloud.web.activity.Activity;
import io.hotcloud.web.activity.ActivityQuery;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.statistics.Statistics;
import io.hotcloud.web.statistics.StatisticsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class UserIndexController {

    private final StatisticsService statisticsService;
    private final ActivityQuery activityQuery;

    public UserIndexController(StatisticsService statisticsService, ActivityQuery activityQuery) {
        this.statisticsService = statisticsService;
        this.activityQuery = activityQuery;
    }

    @RequestMapping(value = {"/index", "/"})
    @WebSession
    public String indexPage(Model model,
                            @CookieUser User user) {
        Statistics statistics = statisticsService.statistics(user.getId());
        PageResult<ActivityLog> pageResult = activityQuery.pagingQuery(user.getUsername(), null, null, Pageable.of(1, 8));
        List<Activity> activities = pageResult.getData().stream().map(this::toActivity).toList();
        model.addAttribute(WebConstant.STATISTICS, statistics);
        model.addAttribute(WebConstant.ACTIVITIES, activities);
        return Views.INDEX;
    }

    private Activity toActivity(ActivityLog log) {
        Activity activity = Activity.builder().build();
        BeanUtils.copyProperties(log, activity);

        return activity;
    }
}
