package io.hotcloud.vendor.registry.model.quay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuayTagSearchResult {

    @JsonProperty("has_additional")
    private boolean hasAdditional;

    @JsonProperty("page")
    private int page;

    @JsonProperty("tags")
    private List<QuayRepositoryTag> tags;
}