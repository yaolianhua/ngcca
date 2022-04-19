package io.hotcloud.web.controller;

import io.hotcloud.security.api.login.BearerToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class IndexController {
    private final ClientAuthorizationManager authorizationManager;

    public IndexController(ClientAuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @RequestMapping(value = {"/index", "/"})
    public String indexPage(HttpServletRequest request,
                            @ModelAttribute("user") User user,
                            Model model) {
        String authorization = authorizationManager.getAuthorization(request.getSession().getId());
        if (!StringUtils.hasText(authorization)) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("authorization", authorization);
        return "index";
    }
}
