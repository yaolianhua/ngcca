package io.hotcloud.vendor.registry.model.quay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuayRepository {

    @JsonProperty("score")
    private int score;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("popularity")
    private double popularity;

    @JsonProperty("is_public")
    private boolean isPublic;

    @JsonProperty("name")
    private String name;

    @JsonProperty("namespace")
    private Namespace namespace;

    @JsonProperty("description")
    private Object description;

    @JsonProperty("href")
    private String href;

    @JsonProperty("stars")
    private int stars;

    @JsonProperty("title")
    private String title;

    @JsonProperty("last_modified")
    private int lastModified;

    @Data
    public static class Namespace {

        @JsonProperty("score")
        private int score;

        @JsonProperty("kind")
        private String kind;

        @JsonProperty("name")
        private String name;

        @JsonProperty("href")
        private String href;

        @JsonProperty("avatar")
        private Avatar avatar;

        @JsonProperty("title")
        private String title;
    }

    @Data
    public static class Avatar {

        @JsonProperty("color")
        private String color;

        @JsonProperty("kind")
        private String kind;

        @JsonProperty("name")
        private String name;

        @JsonProperty("hash")
        private String hash;
    }
}