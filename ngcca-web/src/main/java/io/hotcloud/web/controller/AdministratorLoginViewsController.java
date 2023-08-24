package io.hotcloud.web.controller;

import io.hotcloud.common.model.activity.Action;
import io.hotcloud.common.model.activity.Target;
import io.hotcloud.service.security.login.BearerToken;
import io.hotcloud.service.security.login.LoginApi;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.web.mvc.Log;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebCookie;
import io.hotcloud.web.views.AdminViews;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/login")
public class AdministratorLoginViewsController {

    private final LoginApi loginApi;
    private final UserApi userApi;

    public AdministratorLoginViewsController(LoginApi loginApi, UserApi userApi) {
        this.loginApi = loginApi;
        this.userApi = userApi;
    }

    @GetMapping
    public String adminLoginPage() {
        return AdminViews.ADMIN_LOGIN;
    }

    @PostMapping
    @Log(action = Action.LOGIN, target = Target.USER, activity = "用户登录")
    public String adminLogin(Model model,
                             HttpServletResponse response,
                             @ModelAttribute("username") String username,
                             @ModelAttribute("password") String password) {
        try {
            BearerToken bearerToken = loginApi.basicLogin(username, password);
            if (!userApi.isAdmin(username)) {
                model.addAttribute(WebConstant.MESSAGE, "non-admin account");
                return AdminViews.ADMIN_LOGIN;
            } else {
                Cookie cookie = WebCookie.generate(bearerToken.getAuthorization());
                response.addCookie(cookie);
                return AdminViews.REDIRECT_ADMIN_INDEX;
            }
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return AdminViews.ADMIN_LOGIN;
        }

    }

}
