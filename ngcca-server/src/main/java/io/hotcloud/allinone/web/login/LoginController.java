package io.hotcloud.allinone.web.login;

import io.hotcloud.allinone.web.mvc.WebConstant;
import io.hotcloud.allinone.web.mvc.WebCookie;
import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.login.LoginApi;
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
@RequestMapping
public class LoginController {

    private final LoginApi loginApi;

    public LoginController(LoginApi loginApi) {
        this.loginApi = loginApi;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
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

            return "redirect:/index";
        } catch (Exception e) {
            model.addAttribute(WebConstant.MESSAGE, e.getMessage());
            return "login";

        }

    }

}
