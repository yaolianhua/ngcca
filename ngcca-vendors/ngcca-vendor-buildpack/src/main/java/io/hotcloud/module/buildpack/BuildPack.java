package io.hotcloud.module.buildpack;

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

    private String uuid;

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

    /**
     * imagebuild artifact e.g. 127.0.0.1:5000/image-build/imagebuild:v1
     */
    private String artifact;
    /**
     * Binary package url e.g. <a href="http://127.0.0.1:9009/bucket/java.jar">http://127.0.0.1:9009/bucket/java.jar</a>
     */
    private String packageUrl;

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
