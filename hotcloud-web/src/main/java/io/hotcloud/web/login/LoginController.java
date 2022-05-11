package io.hotcloud.web.login;

import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.user.User;
import io.hotcloud.web.R;
import io.hotcloud.web.WebConstant;
import io.hotcloud.web.WebCookie;
import io.hotcloud.web.user.UserClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class LoginController {

    private final LoginClient loginClient;
    private final UserClient userClient;

    public LoginController(LoginClient loginClient,
                           UserClient userClient) {
        this.loginClient = loginClient;
        this.userClient = userClient;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(Model model,
                        RedirectAttributes redirect,
                        HttpServletResponse response,
                        @ModelAttribute("username") String username,
                        @ModelAttribute("password") String password) {
        ResponseEntity<R<BearerToken>> entity = loginClient.login(username, password);
        R<BearerToken> bearerTokenR = Objects.requireNonNull(entity.getBody());
        boolean successful = entity.getStatusCode().is2xxSuccessful();
        if (successful) {
            String authorization = bearerTokenR.getData().getAuthorization();
            R<User> body = userClient.user(username).getBody();
            redirect.addFlashAttribute(WebConstant.USER, Objects.requireNonNull(body).getData());
            redirect.addFlashAttribute(WebConstant.AUTHORIZATION, authorization);

            Cookie cookie = WebCookie.generate(authorization);
            response.addCookie(cookie);

            return "redirect:/index";
        }

        model.addAttribute(WebConstant.MESSAGE, bearerTokenR.getMessage());
        return "login";
    }

}
