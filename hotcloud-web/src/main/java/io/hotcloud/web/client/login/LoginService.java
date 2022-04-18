package io.hotcloud.web.client.login;

import io.hotcloud.security.api.BearerToken;
import io.hotcloud.security.user.model.User;
import io.hotcloud.web.client.ClientAuthorizationManager;
import io.hotcloud.web.client.R;
import io.hotcloud.web.client.user.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class LoginService {
    private final LoginClient loginClient;
    private final UserClient userClient;
    private final ClientAuthorizationManager authorizationManager;

    public LoginService(LoginClient loginClient,
                        UserClient userClient,
                        ClientAuthorizationManager authorizationManager) {
        this.loginClient = loginClient;
        this.userClient = userClient;
        this.authorizationManager = authorizationManager;
    }

    public String login(Model model, RedirectAttributes redirect, HttpServletRequest request,
                        String username,
                        String password,
                        boolean adminLogin) {
        ResponseEntity<R<BearerToken>> entity = loginClient.login(username, password);
        boolean successful = entity.getStatusCode().is2xxSuccessful();
        if (successful) {
            BearerToken bearerToken = Objects.requireNonNull(entity.getBody()).getData();
            authorizationManager.add(request.getSession().getId(), bearerToken.getAuthorization());

            R<User> body = userClient.user(username).getBody();
            redirect.addFlashAttribute("user", Objects.requireNonNull(body).getData());
            redirect.addFlashAttribute("authorization", Objects.requireNonNull(entity.getBody()).getData().getAuthorization());

            if (adminLogin) {
                if (!isAdmin(username)) {
                    model.addAttribute("message", "non-admin account");
                    return "admin/login";
                } else {
                    return "redirect:/administrator/index";
                }
            }
            return "redirect:/index";
        }

        model.addAttribute("message", "invalid username or password");
        return adminLogin ? "admin/login" : "login";
    }

    private boolean isAdmin(String username) {
        return Objects.equals("admin", username);
    }
}
