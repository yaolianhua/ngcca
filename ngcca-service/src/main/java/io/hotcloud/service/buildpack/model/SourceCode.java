package io.hotcloud.service.buildpack.model;

import io.hotcloud.common.model.JavaRuntime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceCode {

    private String httpGitUrl;
    @Builder.Default
    private String branch = "master";
    private String submodule;
    /**
     * e.g. "-Xms128m -Xmx512m"
     */
    private String startOptions;
    /**
     * e.g. -Dspring.profiles.active=production
     */
    private String startArgs;
    private JavaRuntime runtime;
}
