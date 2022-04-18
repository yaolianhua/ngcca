package io.hotcloud.web.controller;

import io.hotcloud.web.client.login.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/administrator/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(Model model,
                        RedirectAttributes redirect,
                        HttpServletRequest request,
                        @ModelAttribute("username") String username,
                        @ModelAttribute("password") String password) {
        return loginService.login(model, redirect, request, username, password, false);
    }

    @PostMapping("/administrator/login")
    public String adminLogin(Model model,
                             RedirectAttributes redirect,
                             HttpServletRequest request,
                             @ModelAttribute("username") String username,
                             @ModelAttribute("password") String password) {
        return loginService.login(model, redirect, request, username, password, true);
    }

}
