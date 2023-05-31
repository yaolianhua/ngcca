package io.hotcloud.web.mvc;

import io.hotcloud.web.Views;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorViewController implements ErrorController {


    @RequestMapping("/error")
    public String handleError() {
        return Views.REDIRECT_INDEX;
    }
}
