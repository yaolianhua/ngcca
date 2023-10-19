package io.hotcloud.service.cluster.statistic;

import io.hotcloud.service.cluster.KubernetesCluster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllClusterStatistics implements Serializable {

    @Builder.Default
    private List<KubernetesCluster> clusters = new ArrayList<>();
    @Builder.Default
    private List<NodeMetrics> nodeMetrics = new ArrayList<>();
    @Builder.Default
    private List<PodMetrics> podMetrics = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> pods = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> deployments = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> jobs = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> cronJobs = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> daemonSets = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> statefulSets = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> services = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> configMaps = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> secrets = new ArrayList<>();
    @Builder.Default
    private List<WorkloadObject> ingresses = new ArrayList<>();

    public long getTotalNode() {
        return nodeMetrics.size();
    }

    public long getTotalPod() {
        return pods.size();
    }

    public long getTotalDeployment() {
        return deployments.size();
    }

    public long getTotalJob() {
        return jobs.size();
    }

    public long getTotalCronJob() {
        return cronJobs.size();
    }

    public long getTotalDaemonSet() {
        return daemonSets.size();
    }

    public long getTotalStatefulSet() {
        return statefulSets.size();
    }

    public long getTotalService() {
        return services.size();
    }

    public long getTotalSecret() {
        return secrets.size();
    }

    public long getTotalConfigMap() {
        return configMaps.size();
    }

    public long getTotalIngress() {
        return ingresses.size();
    }


    public long getTotalCluster() {
        return clusters.size();
    }
    public long getTotalCpuMilliCoresCapacity() {
        return this.nodeMetrics
                .stream()
                .map(NodeMetrics::getCpuMilliCoresCapacity)
                .reduce(0L, Long::sum);
    }

    public long getTotalMemoryMegabyteCapacity() {
        return this.nodeMetrics
                .stream()
                .map(NodeMetrics::getMemoryMegabyteCapacity)
                .reduce(0L, Long::sum);
    }

    public long getTotalCpuMilliCoresUsage() {
        return this.nodeMetrics
                .stream()
                .map(NodeMetrics::getCpuMilliCoresUsage)
                .reduce(0L, Long::sum);
    }

    public long getTotalMemoryMegabyteUsage() {
        return this.nodeMetrics
                .stream()
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
