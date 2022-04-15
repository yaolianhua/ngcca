package io.hotcloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yaolianhua789@gmail.com
 **/
@Controller
@RequestMapping
public class IndexController {

    @RequestMapping(value = {"/index", "/"})
    public String indexPage() {
        return "index";
    }
}
