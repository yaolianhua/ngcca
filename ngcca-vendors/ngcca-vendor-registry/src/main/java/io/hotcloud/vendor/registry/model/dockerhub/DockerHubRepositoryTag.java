package io.hotcloud.vendor.registry.model.dockerhub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DockerHubRepositoryTag {
    @JsonProperty("last_updater_username")
    private String lastUpdaterUsername;

    @JsonProperty("images")
    private List<Images> images;

    @JsonProperty("creator")
    private int creator;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("tag_last_pulled")
    private String tagLastPulled;

    @JsonProperty("repository")
    private int repository;

    @JsonProperty("last_updater")
    private int lastUpdater;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag_last_pushed")
    private String tagLastPushed;

    @JsonProperty("id")
    private int id;

    @JsonProperty("full_size")
    private int fullSize;

    @JsonProperty("v2")
    private String v2;

    @JsonProperty("status")
    private String status;

    @Data
    public static class Images {

        @JsonProperty("features")
        private String features;

        @JsonProperty("os_features")
        private String osFeatures;

        @JsonProperty("os")
        private String os;

        @JsonProperty("size")
        private int size;

        @JsonProperty("os_version")
        private String osVersion;

        @JsonProperty("variant")
        private String variant;

        @JsonProperty("digest")
        private String digest;

        @JsonProperty("layers")
        private List<LayersItem> layers;

        @JsonProperty("last_pushed")
        private String lastPushed;

        @JsonProperty("last_pulled")
        private String lastPulled;

        @JsonProperty("architecture")
        private String architecture;

        @JsonProperty("status")
        private String status;
    }

    @Data
    public static class LayersItem {

        @JsonProperty("size")
        private int size;

        @JsonProperty("instruction")
        private String instruction;

        @JsonProperty("digest")
        private String digest;
    }
}
