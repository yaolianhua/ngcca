package io.hotcloud.allinone.web.admin;

import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebCookie;
import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.login.LoginApi;
import io.hotcloud.security.api.user.UserApi;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator/login")
public class AdminLoginController {

    private final LoginApi loginApi;
    private final UserApi userApi;

    public AdminLoginController(LoginApi loginApi, UserApi userApi) {
        this.loginApi = loginApi;
        this.userApi = userApi;
    }

    @GetMapping
    public String adminLoginPage() {
        return "admin/login";
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
                return "admin/login";
            } else {
                Cookie cookie = WebCookie.generate(bearerToken.getAuthorization());
                response.addCookie(cookie);
                return "redirect:/administrator/index";
            }
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return "admin/login";
        }

    }

}
