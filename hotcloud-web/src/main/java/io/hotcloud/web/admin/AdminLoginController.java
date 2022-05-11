package io.hotcloud.web.admin;

import io.hotcloud.security.api.login.BearerToken;
import io.hotcloud.security.api.user.User;
import io.hotcloud.web.ClientAuthorizationManager;
import io.hotcloud.web.R;
import io.hotcloud.web.login.LoginClient;
import io.hotcloud.web.user.UserClient;
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
@RequestMapping("/administrator/login")
public class AdminLoginController {

    private final LoginClient loginClient;
    private final UserClient userClient;
    private final ClientAuthorizationManager authorizationManager;

    public AdminLoginController(LoginClient loginClient,
                                UserClient userClient,
                                ClientAuthorizationManager authorizationManager) {
        this.loginClient = loginClient;
        this.userClient = userClient;
        this.authorizationManager = authorizationManager;
    }

    @GetMapping
    public String adminLoginPage() {
        return "admin/login";
    }

    @PostMapping
    public String adminLogin(Model model,
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

            if (!isAdmin(username)) {
                model.addAttribute("message", "non-admin account");
                return "admin/login";
            } else {
                return "redirect:/administrator/index";
            }
        }

        model.addAttribute("message", Objects.requireNonNull(entity.getBody()).getMessage());
        return "admin/login";
    }

    private boolean isAdmin(String username) {
        return Objects.equals("admin", username);
    }

}
