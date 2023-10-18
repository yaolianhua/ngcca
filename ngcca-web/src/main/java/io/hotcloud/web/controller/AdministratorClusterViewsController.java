package io.hotcloud.web.controller;

import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.statistic.KubernetesClusterStatisticsService;
import io.hotcloud.web.mvc.WebConstant;
import io.hotcloud.web.mvc.WebSession;
import io.hotcloud.web.views.AdminViews;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/administrator/cluster")
public class AdministratorClusterViewsController {

    private final KubernetesClusterStatisticsService kubernetesClusterStatisticsService;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    public AdministratorClusterViewsController(KubernetesClusterStatisticsService kubernetesClusterStatisticsService,
                                               DatabasedKubernetesClusterService databasedKubernetesClusterService) {
        this.kubernetesClusterStatisticsService = kubernetesClusterStatisticsService;
        this.databasedKubernetesClusterService = databasedKubernetesClusterService;
    }

    @RequestMapping({"/", ""})
    @WebSession
    public String clusters(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, databasedKubernetesClusterService.list());
        return AdminViews.Cluster.CLUSTER_LIST;
    }

    @RequestMapping("/node")
    @WebSession
    public String nodeList(Model model,
                           @RequestParam(value = "action", required = false) String action) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.allCacheStatistics().getNodeMetrics());
        return Objects.equals(WebConstant.VIEW_LIST, action)
                ? AdminViews.Cluster.CLUSTER_NODE_LIST_FRAGMENT
                : AdminViews.Cluster.CLUSTER_NODE_LIST;
    }

    @RequestMapping("/pod")
    @WebSession
    public String podList(Model model) {
        model.addAttribute(WebConstant.COLLECTION_RESULT, kubernetesClusterStatisticsService.allCacheStatistics().getPodMetrics());
        return AdminViews.Cluster.CLUSTER_POD_LIST;
    }
}
