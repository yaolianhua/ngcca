package io.hotcloud.web.controller;

import io.hotcloud.service.cluster.KubernetesClusterStatisticsService;
import io.hotcloud.web.AdminViews;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administrator/k8s-resource")
public class AdministratorKubernetesResourceViewsController {

    private final KubernetesClusterStatisticsService kubernetesClusterStatisticsService;

    public AdministratorKubernetesResourceViewsController(KubernetesClusterStatisticsService kubernetesClusterStatisticsService) {
        this.kubernetesClusterStatisticsService = kubernetesClusterStatisticsService;
    }

    @RequestMapping("/node-list")
    @WebSession
    public String nodeList(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.statistics().getNodeMetrics());
        return AdminViews.Cluster.CLUSTER_NODE_LIST;
    }

    @RequestMapping("/pod-list")
    @WebSession
    public String podList(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.statistics().getPodMetrics());
        return AdminViews.Cluster.CLUSTER_POD_LIST;
    }
}
