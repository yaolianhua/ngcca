package io.hotcloud.web.service;

import io.hotcloud.service.application.model.ApplicationInstanceStatistics;
import io.hotcloud.service.buildpack.model.BuildPackStatistics;
import io.hotcloud.service.cluster.statistic.ClusterListStatistics;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.template.TemplateInstanceStatistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics implements Serializable {

    private User user;
    private String namespace;

    @Builder.Default
    private Collection<User> users = new ArrayList<>();

    @Builder.Default
    private TemplateInstanceStatistics templates = new TemplateInstanceStatistics();

    @Builder.Default
    private BuildPackStatistics buildPacks = new BuildPackStatistics();
    @Builder.Default
    private ApplicationInstanceStatistics applications = new ApplicationInstanceStatistics();
    @Builder.Default
    private ClusterListStatistics clusterStatistics = new ClusterListStatistics();

}
