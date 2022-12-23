package io.hotcloud.allinone.web.statistics;

import io.hotcloud.application.api.core.ApplicationInstanceStatistics;
import io.hotcloud.application.api.template.TemplateInstanceStatistics;
import io.hotcloud.buildpack.api.clone.GitClonedStatistics;
import io.hotcloud.buildpack.api.core.BuildPackStatistics;
import io.hotcloud.security.api.user.User;
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
