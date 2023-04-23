package io.hotcloud.vendor.registry.model.harbor;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class HarborSearchResult {

    private List<HarborRepository> repository = new LinkedList<>();
    private List<HarborProject> project = new LinkedList<>();
}