package io.hotcloud.common.api.core.registry.model.harbor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HarborArtifact {

    @JsonProperty("addition_links")
    private AdditionLinks additionLinks;

    @JsonProperty("references")
    private Object references;

    @JsonProperty("extra_attrs")
    private ExtraAttrs extraAttrs;

    @JsonProperty("icon")
    private String icon;

    @JsonProperty("manifest_media_type")
    private String manifestMediaType;

    @JsonProperty("type")
    private String type;

    @JsonProperty("labels")
    private Object labels;

    @JsonProperty("tags")
    private List<TagsItem> tags;

    @JsonProperty("pull_time")
    private String pullTime;

    @JsonProperty("size")
    private int size;

    @JsonProperty("media_type")
    private String mediaType;

    @JsonProperty("project_id")
    private int projectId;

    @JsonProperty("digest")
    private String digest;

    @JsonProperty("repository_id")
    private int repositoryId;

    @JsonProperty("id")
    private int id;

    @JsonProperty("push_time")
    private String pushTime;

    @Data
    public static class AdditionLinks {

        @JsonProperty("vulnerabilities")
        private Vulnerabilities vulnerabilities;

        @JsonProperty("build_history")
        private BuildHistory buildHistory;
    }

    @Data
    public static class Vulnerabilities {

        @JsonProperty("absolute")
        private boolean absolute;

        @JsonProperty("href")
        private String href;
    }

    @Data
    public static class BuildHistory {

        @JsonProperty("absolute")
        private boolean absolute;

        @JsonProperty("href")
        private String href;
    }

    @Data
    public static class ExtraAttrs {

        @JsonProperty("os")
        private String os;

        @JsonProperty("author")
        private Object author;

        @JsonProperty("created")
        private String created;

        @JsonProperty("architecture")
        private String architecture;
    }

    @Data
    public static class TagsItem {

        @JsonProperty("pull_time")
        private String pullTime;

        @JsonProperty("immutable")
        private boolean immutable;

        @JsonProperty("name")
        private String name;

        @JsonProperty("signed")
        private boolean signed;

        @JsonProperty("repository_id")
        private int repositoryId;

        @JsonProperty("id")
        private int id;

        @JsonProperty("artifact_id")
        private int artifactId;

        @JsonProperty("push_time")
        private String pushTime;
    }
}