package io.hotcloud.common.model.registry.quay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuayRepositoryTag {

    @JsonProperty("reversion")
    private boolean reversion;

    @JsonProperty("start_ts")
    private int startTs;

    @JsonProperty("size")
    private int size;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("last_modified")
    private String lastModified;

    @JsonProperty("docker_image_id")
    private String dockerImageId;

    @JsonProperty("manifest_digest")
    private String manifestDigest;

    @JsonProperty("is_manifest_list")
    private boolean isManifestList;
}