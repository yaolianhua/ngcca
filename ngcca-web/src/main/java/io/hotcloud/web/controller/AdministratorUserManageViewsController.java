package io.hotcloud.web.controller;

import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.service.security.user.UserCollectionQuery;
import io.hotcloud.web.AdminViews;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/administrator/user-manage")
public class AdministratorUserManageViewsController {

    private final UserApi userApi;
    private final UserCollectionQuery userCollectionQuery;
    private final StatisticsService statisticsService;

    public AdministratorUserManageViewsController(UserApi userApi, UserCollectionQuery userCollectionQuery, StatisticsService statisticsService) {
        this.userApi = userApi;
        this.userCollectionQuery = userCollectionQuery;
        this.statisticsService = statisticsService;
    }

    @RequestMapping
    @WebSession
    public String users(Model model,
                        @RequestParam(value = "action", required = false) String action,
                        @RequestParam(value = "id", required = false) String userid,
                        @RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "enabled", required = false) Boolean enabled) {
        if (Objects.equals(WebConstant.VIEW_LIST, action)) {
            model.addAttribute(WebConstant.PAGE_RESULT, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
            return AdminViews.UserManage.USER_MANAGE_LIST_FRAGMENT;
        }
        if (Objects.equals(WebConstant.VIEW_EDIT, action)) {
            model.addAttribute(WebConstant.USER, userApi.find(userid));
            return AdminViews.UserManage.USER_MANAGE_EDIT_FRAGMENT;
        }
        if (Objects.equals(WebConstant.VIEW_DETAIL, action)) {
            model.addAttribute(WebConstant.STATISTICS, statisticsService.statistics(userid));
            model.addAttribute(WebConstant.USER, userApi.find(userid));
            return AdminViews.UserManage.USER_MANAGE_DETAIL_FRAGMENT;
        }

        model.addAttribute(WebConstant.PAGE_RESULT, userCollectionQuery.pagingQuery(username, enabled, Pageable.of(1, Integer.MAX_VALUE)));
        return AdminViews.UserManage.USER_MANAGE;
    }

}
