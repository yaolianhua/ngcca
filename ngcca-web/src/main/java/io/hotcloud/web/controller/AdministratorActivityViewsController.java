package io.hotcloud.web.controller;

import io.hotcloud.common.model.Pageable;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.ActivityQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/activity")
public class AdministratorActivityViewsController {

    private final ActivityQuery activityQuery;

    public AdministratorActivityViewsController(ActivityQuery activityQuery) {
        this.activityQuery = activityQuery;
    }

    @RequestMapping("/activity-list")
    @WebSession
    public String activities(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, activityQuery.pagingQuery(null, null, null, new Pageable(1, Integer.MAX_VALUE)).getData());
        return Views.ADMIN_ACTIVITY_LIST;
    }

}
