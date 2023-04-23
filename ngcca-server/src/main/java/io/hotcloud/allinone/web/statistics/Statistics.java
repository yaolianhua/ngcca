package io.hotcloud.allinone.web.statistics;

import io.hotcloud.module.application.core.ApplicationInstanceStatistics;
import io.hotcloud.module.application.template.TemplateInstanceStatistics;
import io.hotcloud.module.buildpack.BuildPackStatistics;
import io.hotcloud.module.buildpack.GitClonedStatistics;
import io.hotcloud.module.security.user.User;
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
    private GitClonedStatistics repositories = new GitClonedStatistics();
    @Builder.Default
    private BuildPackStatistics buildPacks = new BuildPackStatistics();
    @Builder.Default
    private ApplicationInstanceStatistics applications = new ApplicationInstanceStatistics();

}
