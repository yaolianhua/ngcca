package io.hotcloud.web.administrator;

import io.hotcloud.module.security.login.BearerToken;
import io.hotcloud.module.security.login.LoginApi;
import io.hotcloud.module.security.user.UserApi;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebCookie;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator/login")
public class LoginViewsController {

    private final LoginApi loginApi;
    private final UserApi userApi;

    public LoginViewsController(LoginApi loginApi, UserApi userApi) {
        this.loginApi = loginApi;
        this.userApi = userApi;
    }

    @GetMapping
    public String adminLoginPage() {
        return Views.ADMIN_LOGIN;
    }

    @PostMapping
    public String adminLogin(Model model,
                             HttpServletResponse response,
                             @ModelAttribute("username") String username,
                             @ModelAttribute("password") String password) {
        try {
            BearerToken bearerToken = loginApi.basicLogin(username, password);
            if (!userApi.isAdmin(username)) {
                model.addAttribute(WebConstant.MESSAGE, "non-admin account");
                return Views.ADMIN_LOGIN;
            } else {
                Cookie cookie = WebCookie.generate(bearerToken.getAuthorization());
                response.addCookie(cookie);
                return Views.REDIRECT_ADMIN_INDEX;
            }
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return Views.ADMIN_LOGIN;
        }

    }

}
