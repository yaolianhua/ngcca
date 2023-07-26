package io.hotcloud.web.controller;

import io.hotcloud.service.registry.RegistryImageQueryService;
import io.hotcloud.web.Views;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/registryimage")
public class AdministratorRegistryImageViewsController {

    private final RegistryImageQueryService registryImageQueryService;

    public AdministratorRegistryImageViewsController(RegistryImageQueryService registryImageQueryService) {
        this.registryImageQueryService = registryImageQueryService;
    }

    @RequestMapping
    @WebSession
    public String registryimages(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, registryImageQueryService.list());
        return Views.REGISTRY_IMAGE_LIST;
    }
}
