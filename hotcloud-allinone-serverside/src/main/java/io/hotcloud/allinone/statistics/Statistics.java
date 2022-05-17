package io.hotcloud.allinone.statistics;

import io.hotcloud.application.api.template.InstanceTemplateStatistics;
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

    private InstanceTemplateStatistics templates;
    private GitClonedStatistics repositories;
    private BuildPackStatistics buildPacks;
}
