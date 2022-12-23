package io.hotcloud.allinone.web.user;

import io.hotcloud.allinone.web.activity.Activity;
import io.hotcloud.allinone.web.activity.ActivityQuery;
import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebUser;
import io.hotcloud.allinone.web.statistics.Statistics;
import io.hotcloud.allinone.web.statistics.StatisticsService;
import io.hotcloud.common.api.activity.ActivityLog;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
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
    private final UserApi userApi;

    public UserIndexController(StatisticsService statisticsService, ActivityQuery activityQuery, UserApi userApi) {
        this.statisticsService = statisticsService;
        this.activityQuery = activityQuery;
        this.userApi = userApi;
    }

    @RequestMapping(value = {"/index", "/"})
    @WebUser
    public String indexPage(Model model,
                            User user) {
        Statistics statistics = statisticsService.statistics(user.getId());
        PageResult<ActivityLog> pageResult = activityQuery.pagingQuery(user.getUsername(), null, null, Pageable.of(1, 20));
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
