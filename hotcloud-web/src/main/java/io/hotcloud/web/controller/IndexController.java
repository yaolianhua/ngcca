package io.hotcloud.web.controller;

import io.hotcloud.security.api.login.BearerToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class IndexController {

    @RequestMapping(value = {"/index", "/"})
    public String indexPage(@ModelAttribute("authorization") BearerToken bearerToken, Model model) {
        if (Objects.isNull(bearerToken) || !StringUtils.hasText(bearerToken.getAuthorization())) {
            return "redirect:/login";
        }

        model.addAttribute("authorization", bearerToken);
        return "index";
    }
}
