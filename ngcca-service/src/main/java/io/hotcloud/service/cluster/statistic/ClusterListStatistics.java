package io.hotcloud.service.cluster.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterListStatistics implements Serializable {

    @Builder.Default
    private List<ClusterStatistics> items = new ArrayList<>();

    public List<NodeImage> getNodeImages() {
        return this.getNodeMetrics()
                .stream()
                .flatMap(e -> e.getImages().stream())
                .collect(Collectors.toList());

    }
    public List<NodeMetrics> getNodeMetrics() {
        return items.stream()
                .flatMap(e -> e.getNodeMetrics().stream())
                .collect(Collectors.toList());
    }

    public List<PodMetrics> getPodMetrics() {
        return items.stream()
                .flatMap(e -> e.getPodMetrics().stream())
                .collect(Collectors.toList());
    }
    public long getTotalNode() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalNode)
                .sum();
    }

    public long getTotalPod() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalPod)
                .sum();
    }

    public long getTotalDeployment() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalDeployment)
                .sum();
    }

    public long getTotalJob() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalJob)
                .sum();
    }

    public long getTotalCronJob() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalCronJob)
                .sum();
    }

    public long getTotalDaemonSet() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalDaemonSet)
                .sum();
    }

    public long getTotalStatefulSet() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalStatefulSet)
                .sum();
    }

    public long getTotalService() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalService)
                .sum();
    }

    public long getTotalSecret() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalSecret)
                .sum();
    }

    public long getTotalConfigMap() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalConfigMap)
                .sum();
    }

    public long getTotalIngress() {
        return items.stream()
                .mapToLong(ClusterStatistics::getTotalIngress)
                .sum();
    }


    public long getTotalCluster() {
        return items.size();
    }
    public long getTotalCpuMilliCoresCapacity() {
        return items.stream()
                .flatMap(e -> e.getNodeMetrics().stream())
                .map(NodeMetrics::getCpuMilliCoresCapacity)
                .reduce(0L, Long::sum);
    }

    public long getTotalMemoryMegabyteCapacity() {
        return items.stream()
                .flatMap(e -> e.getNodeMetrics().stream())
                .map(NodeMetrics::getMemoryMegabyteCapacity)
                .reduce(0L, Long::sum);
    }

    public long getTotalCpuMilliCoresUsage() {
        return items.stream()
                .flatMap(e -> e.getNodeMetrics().stream())
                .map(NodeMetrics::getCpuMilliCoresUsage)
                .reduce(0L, Long::sum);
    }

    public long getTotalMemoryMegabyteUsage() {
        return items.stream()
                .flatMap(e -> e.getNodeMetrics().stream())
                .map(NodeMetrics::getMemoryMegabyteUsage)
                .reduce(0L, Long::sum);
    }

    public double getTotalCpuUsagePercentage() {
        long totalCpuMilliCoresUsage = getTotalCpuMilliCoresUsage();
        long totalCpuMilliCoresCapacity = getTotalCpuMilliCoresCapacity();
        double percentage = (double) totalCpuMilliCoresUsage / totalCpuMilliCoresCapacity * 100;
        return Double.parseDouble(new DecimalFormat("0.00").format(percentage));
    }

    public double getTotalMemoryUsagePercentage() {
        long totalMemoryMegabyteUsage = getTotalMemoryMegabyteUsage();
        long totalMemoryMegabyteCapacity = getTotalMemoryMegabyteCapacity();
        double percentage = (double) totalMemoryMegabyteUsage / totalMemoryMegabyteCapacity * 100;
        return Double.parseDouble(new DecimalFormat("0.00").format(percentage));
    }
}
