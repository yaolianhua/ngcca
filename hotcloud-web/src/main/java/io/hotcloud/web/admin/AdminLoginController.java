package io.hotcloud.web.admin;

import io.hotcloud.web.login.LoginClient;
import io.hotcloud.web.mvc.BearerToken;
import io.hotcloud.web.mvc.R;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/administrator/login")
public class AdminLoginController {

    private final LoginClient loginClient;

    public AdminLoginController(LoginClient loginClient) {
        this.loginClient = loginClient;
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
        ResponseEntity<R<BearerToken>> entity = loginClient.login(username, password);
        R<BearerToken> bearerTokenR = Objects.requireNonNull(entity.getBody());
        boolean successful = entity.getStatusCode().is2xxSuccessful();
        if (successful) {
            if (!isAdmin(username)) {
                model.addAttribute(WebConstant.MESSAGE, "non-admin account");
                return "admin/login";
            } else {
                Cookie cookie = WebCookie.generate(bearerTokenR.getData().getAuthorization());
                response.addCookie(cookie);
                return "redirect:/administrator/index";
            }
        }

        model.addAttribute(WebConstant.MESSAGE, bearerTokenR.getMessage());
        return "admin/login";
    }

    private boolean isAdmin(String username) {
        return Objects.equals("admin", username);
    }

}
