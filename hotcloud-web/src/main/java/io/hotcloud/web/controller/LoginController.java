package io.hotcloud.web.controller;

import io.hotcloud.security.api.BearerToken;
import io.hotcloud.security.user.model.User;
import io.hotcloud.web.client.ClientAuthorizationManager;
import io.hotcloud.web.client.R;
import io.hotcloud.web.client.login.LoginClient;
import io.hotcloud.web.client.user.UserClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping("/login")
public class LoginController {

    private final LoginClient loginClient;
    private final UserClient userClient;
    private final ClientAuthorizationManager authorizationManager;

    public LoginController(@Qualifier("io.hotcloud.web.client.login.LoginClient") LoginClient loginClient,
                           @Qualifier("io.hotcloud.web.client.user.UserClient") UserClient userClient,
                           ClientAuthorizationManager authorizationManager) {
        this.loginClient = loginClient;
        this.userClient = userClient;
        this.authorizationManager = authorizationManager;
    }

    @GetMapping
    public String loginPage() {
        return "login";
    }

    @PostMapping
    public String login(Model model,
                        RedirectAttributes redirect,
                        HttpServletRequest request,
                        @ModelAttribute("username") String username,
                        @ModelAttribute("password") String password) {
        ResponseEntity<R<BearerToken>> entity = loginClient.login(username, password);
        boolean successful = entity.getStatusCode().is2xxSuccessful();
        if (successful) {
            BearerToken bearerToken = Objects.requireNonNull(entity.getBody()).getData();
            authorizationManager.add(request.getSession().getId(), bearerToken.getAuthorization());

            R<User> body = userClient.user(username).getBody();
            redirect.addFlashAttribute("user", Objects.requireNonNull(body).getData());
            redirect.addFlashAttribute("authorization", Objects.requireNonNull(entity.getBody()).getData().getAuthorization());

            return isAdmin(username) ? "redirect:/administrator/index" : "redirect:/index";
        }

        model.addAttribute("message", "invalid username or password");
        return "login";
    }

    private boolean isAdmin(String username) {
        return Objects.equals("admin", username);
    }
}
