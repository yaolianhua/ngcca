package io.hotcloud.service.cluster.statistic;

import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeMetrics implements Serializable {
    @Builder.Default
    private KubernetesCluster cluster = new KubernetesCluster();

    private String node;
    private String ip;
    private String status;
    private String containerRuntime;
    private String kubeletVersion;
    private String architecture;
    private String osImage;

    @Builder.Default
    private Map<String, String> labels = new HashMap<>();
    private long cpuMilliCoresUsage;
    private long memoryMegabyteUsage;
    private long cpuMilliCoresCapacity;
    private long memoryMegabyteCapacity;

    public String getLabelShow() {
        return this.labels.entrySet().stream()
                .map(l -> String.format("%s:%s", l.getKey(), l.getValue()))
                .collect(Collectors.joining("<br>"));
    }

    public double getCpuUsagePercentage() {
        if (this.cpuMilliCoresUsage == 0 || this.cpuMilliCoresCapacity == 0) {
            return 0.00;
        }

        double percentage = (double) cpuMilliCoresUsage / cpuMilliCoresCapacity * 100;
        return Double.parseDouble(new DecimalFormat("0.00").format(percentage));
    }

    public double getMemoryUsagePercentage() {
        if (this.memoryMegabyteUsage == 0 || this.memoryMegabyteCapacity == 0) {
            return 0.00;
        }

        double percentage = (double) memoryMegabyteUsage / memoryMegabyteCapacity * 100;
        return Double.parseDouble(new DecimalFormat("0.00").format(percentage));
    }
}
