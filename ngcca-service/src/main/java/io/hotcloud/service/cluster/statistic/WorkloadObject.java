package io.hotcloud.service.cluster.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadObject implements Serializable {
    private String namespace;
    private String name;
}
