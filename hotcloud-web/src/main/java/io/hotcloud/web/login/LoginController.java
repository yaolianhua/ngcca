package io.hotcloud.web.login;

import io.hotcloud.web.mvc.Result;
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
@RequestMapping
public class LoginController {

    private final LoginClient loginClient;

    public LoginController(LoginClient loginClient) {
        this.loginClient = loginClient;
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
        ResponseEntity<Result<BearerToken>> entity = loginClient.login(username, password);
        Result<BearerToken> bearerTokenResult = Objects.requireNonNull(entity.getBody());
        boolean successful = entity.getStatusCode().is2xxSuccessful();
        if (successful) {
            String authorization = bearerTokenResult.getData().getAuthorization();
            Cookie cookie = WebCookie.generate(authorization);
            response.addCookie(cookie);

            return "redirect:/index";
        }

        model.addAttribute(WebConstant.MESSAGE, bearerTokenResult.getMessage());
        return "login";
    }

}
