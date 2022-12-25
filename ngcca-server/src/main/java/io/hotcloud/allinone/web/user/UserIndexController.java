package io.hotcloud.allinone.web.user;

import io.hotcloud.allinone.web.activity.Activity;
import io.hotcloud.allinone.web.activity.ActivityQuery;
import io.hotcloud.allinone.web.mvc.CookieUser;
import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebSession;
import io.hotcloud.allinone.web.statistics.Statistics;
import io.hotcloud.allinone.web.statistics.StatisticsService;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.security.api.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Activity> activities = pageResult.getData().stream().map(this::toActivity).collect(Collectors.toList());
        model.addAttribute(WebConstant.STATISTICS, statistics);
        model.addAttribute(WebConstant.ACTIVITIES, activities);
        return "index";
    }

    private Activity toActivity(ActivityLog log) {
        Activity activity = Activity.builder().build();
        BeanUtils.copyProperties(log, activity);

        return activity;
    }
}
