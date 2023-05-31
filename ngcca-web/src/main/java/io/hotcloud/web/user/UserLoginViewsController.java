package io.hotcloud.web.user;

import io.hotcloud.module.security.login.BearerToken;
import io.hotcloud.module.security.login.LoginApi;
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

@Controller
@RequestMapping
public class UserLoginViewsController {

    private final LoginApi loginApi;

    public UserLoginViewsController(LoginApi loginApi) {
        this.loginApi = loginApi;
    }

    @GetMapping("/login")
    public String loginPage() {
        return Views.LOGIN;
    }

    @PostMapping("/login")
    public String login(Model model,
                        HttpServletResponse response,
                        @ModelAttribute("username") String username,
                        @ModelAttribute("password") String password) {
        try {
            BearerToken bearerToken = loginApi.basicLogin(username, password);
            Cookie cookie = WebCookie.generate(bearerToken.getAuthorization());
            response.addCookie(cookie);

            return Views.REDIRECT_INDEX;
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return Views.LOGIN;

        }

    }

}
