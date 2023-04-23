package io.hotcloud.vendor.registry.model.quay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuayRepositorySearchResult {

    @JsonProperty("start_index")
    private int startIndex;

    @JsonProperty("has_additional")
    private boolean hasAdditional;

    @JsonProperty("page")
    private int page;

    @JsonProperty("results")
    private List<QuayRepository> results;

    @JsonProperty("page_size")
    private int pageSize;
}