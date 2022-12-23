package io.hotcloud.allinone.web.mvc;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
public class GlobalErrorViewController implements ErrorController {


    @RequestMapping("/error")
    public String handleError() {
        return "redirect:/index";
    }
}
