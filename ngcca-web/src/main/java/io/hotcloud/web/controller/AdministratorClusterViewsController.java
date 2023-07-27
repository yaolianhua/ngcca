package io.hotcloud.web.controller;

import io.hotcloud.service.cluster.KubernetesClusterStatisticsService;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.views.AdminViews;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/cluster")
public class AdministratorClusterViewsController {

    private final KubernetesClusterStatisticsService kubernetesClusterStatisticsService;

    public AdministratorClusterViewsController(KubernetesClusterStatisticsService kubernetesClusterStatisticsService) {
        this.kubernetesClusterStatisticsService = kubernetesClusterStatisticsService;
    }

    @RequestMapping("/node")
    @WebSession
    public String nodeList(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.statistics().getNodeMetrics());
        return AdminViews.Cluster.CLUSTER_NODE_LIST;
    }

    @RequestMapping("/pod")
    @WebSession
    public String podList(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.statistics().getPodMetrics());
        return AdminViews.Cluster.CLUSTER_POD_LIST;
    }
}
