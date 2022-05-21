package io.hotcloud.web.user;

import io.hotcloud.web.mvc.Result;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebUser;
import io.hotcloud.web.statistics.Statistics;
import io.hotcloud.web.statistics.StatisticsClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class UserIndexController {

    private final StatisticsClient statisticsClient;

    public UserIndexController(StatisticsClient statisticsClient) {
        this.statisticsClient = statisticsClient;
    }

    @RequestMapping(value = {"/index", "/"})
    @WebUser
    public String indexPage(Model model,
                            User user) {
        Result<Statistics> result = statisticsClient.statistics(user.getId()).getBody();
        model.addAttribute(WebConstant.STATISTICS, Objects.requireNonNull(result).getData());
        return "index";
    }
}
