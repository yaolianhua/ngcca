package io.hotcloud.service.cluster.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PodMetrics {
    private String namespace;
    private String pod;
    @Builder.Default
    private List<Container> containers = new ArrayList<>();
    private String status;
    private RefNode refNode;
    @Builder.Default
    private Set<RefService> refServices = new HashSet<>();
    private long cpuMilliCoresUsage;
    private long memoryMegabyteUsage;

    public boolean onlyOneContainer() {
        return containers.size() == 1;
    }

    public boolean existedPodService() {
        return !refServices.isEmpty();
    }

    public String getServiceShow() {
        StringBuilder show = new StringBuilder();
        for (RefService refService : this.getRefServices()) {
            if (refService == null) {
                return "<none>";
            }

            String svc = "name: " + refService.getName() + "<br>" +
                    "type: " + refService.getType() + "<br>" +
                    "clusterIP: " + refService.getClusterIp() + "<br>" +
                    "ports: " + refService.getPorts() + "<br>" +
                    "------------------------------------------------<br>";
            show.append(svc);
        }

        return show.toString();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefNode {
        private String ip;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefService {
        private String type;
        private String name;
        private String clusterIp;
        private String ports;
    }
}
