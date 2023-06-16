package io.hotcloud.web.service;

import io.hotcloud.module.application.ApplicationInstanceStatistics;
import io.hotcloud.module.application.template.TemplateInstanceStatistics;
import io.hotcloud.module.buildpack.model.BuildPackStatistics;
import io.hotcloud.module.security.user.User;
import io.hotcloud.service.cluster.KubernetesClusterStatistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    private User user;
    private String namespace;

    @Builder.Default
    private TemplateInstanceStatistics templates = new TemplateInstanceStatistics();

    @Builder.Default
    private BuildPackStatistics buildPacks = new BuildPackStatistics();
    @Builder.Default
    private ApplicationInstanceStatistics applications = new ApplicationInstanceStatistics();
    @Builder.Default
    private KubernetesClusterStatistics clusterStatistics = new KubernetesClusterStatistics();

}
