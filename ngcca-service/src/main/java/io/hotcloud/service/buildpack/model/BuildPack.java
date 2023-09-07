package io.hotcloud.service.buildpack.model;

import io.hotcloud.db.entity.BuildPackEntity;
import io.hotcloud.db.model.BuildPackDockerSecretResource;
import io.hotcloud.db.model.BuildPackJobResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
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

    private BuildPackDockerSecretResource secretResource;

    private String user;

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

    private Instant modifiedAt;
    private Instant createdAt;

    public BuildPack() {
    }

    public String getYaml() {
        return this.jobResource.getJobResourceYaml() +
                "\n---\n" +
                this.secretResource.getSecretResourceYaml();
    }

    public Map<String, String> getAlternative() {
        return this.jobResource.getAlternative();
    }

    public static BuildPack toBuildPack(BuildPackEntity entity) {
        return BuildPack.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .jobResource(entity.getJob())
                .secretResource(entity.getSecret())
                .yaml(entity.getYaml())
                .user(entity.getUser())
                .done(entity.isDone())
                .deleted(entity.isDeleted())
                .httpGitUrl(entity.getHttpGitUrl())
                .gitBranch(entity.getGitBranch())
                .message(entity.getMessage())
                .logs(entity.getLogs())
                .artifact(entity.getArtifact())
                .packageUrl(entity.getPackageUrl())
                .createdAt(entity.getCreatedAt())
                .modifiedAt(entity.getModifiedAt())
                .build();
    }
}
