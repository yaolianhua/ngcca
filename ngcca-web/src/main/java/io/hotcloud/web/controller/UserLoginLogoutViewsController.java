package io.hotcloud.web.controller;

import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.security.WebCookie;
import io.hotcloud.service.security.login.BearerToken;
import io.hotcloud.service.security.login.LoginApi;
import io.hotcloud.service.security.user.User;
import io.hotcloud.web.mvc.CookieUser;
import io.hotcloud.web.mvc.Log;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.views.UserViews;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class UserLoginLogoutViewsController {

    private final LoginApi loginApi;

    public UserLoginLogoutViewsController(LoginApi loginApi) {
        this.loginApi = loginApi;
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request,
                            HttpServletResponse response) {
        return UserViews.LOGIN;
    }

    @PostMapping("/login")
    @Log(action = Action.LOGIN, target = Target.USER, activity = "用户登录")
    public String login(Model model,
                        HttpServletResponse response,
                        @ModelAttribute("username") String username,
                        @ModelAttribute("password") String password) {
        try {
            BearerToken bearerToken = loginApi.basicLogin(username, password);
            Cookie cookie = WebCookie.generateAuthorizationCookie(bearerToken.getAuthorization());
            response.addCookie(cookie);

            return UserViews.REDIRECT_INDEX;
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return UserViews.LOGIN;

        }

    }

    @RequestMapping("/user/logout")
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
