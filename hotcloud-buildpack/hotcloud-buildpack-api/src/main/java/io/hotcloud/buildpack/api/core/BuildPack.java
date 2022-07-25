package io.hotcloud.buildpack.api.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class BuildPack {

    private String id;

    private BuildPackJobResource jobResource;

    @Deprecated(since = "BuildPackApiV2")
    @Builder.Default
    private BuildPackStorageResourceList storageResource = new BuildPackStorageResourceList();

    private BuildPackDockerSecretResource secretResource;

    private String user;

    @Deprecated(since = "BuildPackApiV2")
    private String clonedId = "Deprecated";

    private String httpGitUrl;

    private String gitBranch;

    private boolean done;

    private boolean deleted;

    private String message;

    private String logs;

    private String yaml;

    private String artifact;

    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;

    public BuildPack() {
    }

    public String getYaml() {
        return this.jobResource.getJobResourceYaml() +
                "\n---\n" +
                this.secretResource.getSecretResourceYaml();
    }

    public String getClonedId() {
        return StringUtils.hasText(this.clonedId) ? clonedId : "Deprecated";
    }

    public Map<String, String> getAlternative() {
        return this.jobResource.getAlternative();
    }
}
