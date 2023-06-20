package io.hotcloud.web.controller;

import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.Statistics;
import io.hotcloud.web.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator")
public class AdministratorIndexViewsController {

    private final StatisticsService statisticsService;

    public AdministratorIndexViewsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @RequestMapping(value = {"/index", ""})
    @WebSession
    public String indexPage(Model model) {
        Statistics statistics = statisticsService.statistics();

        model.addAttribute(WebConstant.STATISTICS, statistics);

        return Views.ADMIN_INDEX;
    }
}
