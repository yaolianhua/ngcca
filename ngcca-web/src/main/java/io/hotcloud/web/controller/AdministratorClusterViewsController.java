package io.hotcloud.web.controller;

import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.statistic.ClusterListStatisticsService;
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

    private final ClusterListStatisticsService clusterListStatisticsService;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    public AdministratorClusterViewsController(ClusterListStatisticsService clusterListStatisticsService,
                                               DatabasedKubernetesClusterService databasedKubernetesClusterService) {
        this.clusterListStatisticsService = clusterListStatisticsService;
        this.databasedKubernetesClusterService = databasedKubernetesClusterService;
    }

    @RequestMapping({"/", ""})
    @WebSession
    public String clusters(Model model,
                           @RequestParam(value = "action", required = false) String action) {
        model.addAttribute(WebConstant.COLLECTION, databasedKubernetesClusterService.list());
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            return AdminViews.Cluster.CLUSTER_LIST_FRAGMENT;
        }

        return AdminViews.Cluster.CLUSTER_LIST;
    }

    @RequestMapping("/node")
    @WebSession
    public String nodeList(Model model,
                           @RequestParam(value = "action", required = false) String action) {
        model.addAttribute(WebConstant.COLLECTION, clusterListStatisticsService.getClusterListStatisticsFromCache().getNodeMetrics());
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            return AdminViews.Cluster.CLUSTER_NODE_LIST_FRAGMENT;
        }
        return AdminViews.Cluster.CLUSTER_NODE_LIST;
    }

    @RequestMapping("/pod")
    @WebSession
    public String podList(Model model,
                          @RequestParam(value = "action", required = false) String action) {
        model.addAttribute(WebConstant.COLLECTION, clusterListStatisticsService.getClusterListStatisticsFromCache().getPodMetrics());
        if (Objects.equals(WebConstant.VIEW_LIST_FRAGMENT, action)) {
            return AdminViews.Cluster.CLUSTER_POD_LIST_FRAGMENT;
        }
        return AdminViews.Cluster.CLUSTER_POD_LIST;
    }
}
