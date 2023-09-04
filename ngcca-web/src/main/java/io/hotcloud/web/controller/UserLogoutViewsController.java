package io.hotcloud.web.controller;

import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.security.user.User;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.Log;
import io.hotcloud.web.mvc.WebCookie;
import io.hotcloud.web.views.UserViews;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/logout")
public class UserLogoutViewsController {

    @RequestMapping
    @Log(action = Action.LOGOUT, target = Target.USER, activity = "用户登出")
    public String adminLogout(HttpServletRequest request,
                              HttpServletResponse response,
                              @CookieUser User user) {
        try {
            WebCookie.removeAuthorizationCookie(request, response);
        } catch (Exception e) {
            //
        }
        return UserViews.REDIRECT_LOGIN;
    }

}
