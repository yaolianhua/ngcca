package io.hotcloud.web.controller;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.common.model.activity.ALog;
import io.hotcloud.web.AdminViews;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.ActivityQuery;
import io.hotcloud.web.service.Statistics;
import io.hotcloud.web.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/administrator")
public class AdministratorIndexViewsController {

    private final StatisticsService statisticsService;
    private final ActivityQuery activityQuery;

    public AdministratorIndexViewsController(StatisticsService statisticsService,
                                             ActivityQuery activityQuery) {
        this.statisticsService = statisticsService;
        this.activityQuery = activityQuery;
    }

    @RequestMapping(value = {"/index", ""})
    @WebSession
    public String indexPage(Model model) {
        Statistics statistics = statisticsService.statistics();
        PageResult<ALog> pageResult = activityQuery.pagingQuery(null, null, null, Pageable.of(1, 12));
        List<ALog> activities = pageResult.getData().stream().toList();
        model.addAttribute(WebConstant.STATISTICS, statistics);
        model.addAttribute(WebConstant.ACTIVITIES, activities);

        return AdminViews.ADMIN_INDEX;
    }
}
