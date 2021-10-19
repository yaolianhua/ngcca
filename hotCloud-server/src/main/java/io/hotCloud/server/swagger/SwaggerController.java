package io.hotCloud.server.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.boot.starter.autoconfigure.OpenApiAutoConfiguration;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ConditionalOnClass(OpenApiAutoConfiguration.class)
@ApiIgnore
public class SwaggerController {

    @GetMapping("/swagger-ui.html")
    public String swagger() {
        // Redirect from swagger2 view page to swagger3 view page
        return "redirect:/swagger-ui/index.html";
    }
}
