package io.hotcloud.web.service;

import io.hotcloud.service.application.ApplicationInstanceStatistics;
import io.hotcloud.service.application.template.TemplateInstanceStatistics;
import io.hotcloud.service.buildpack.model.BuildPackStatistics;
import io.hotcloud.service.cluster.KubernetesClusterStatistics;
import io.hotcloud.service.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

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
    private KubernetesClusterStatistics clusterStatistics = new KubernetesClusterStatistics();

}
