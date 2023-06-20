package io.hotcloud.service.cluster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KubernetesClusterStatistics {

    private List<NodeMetrics> nodeMetrics = new ArrayList<>();

    private List<PodMetrics> podMetrics = new ArrayList<>();
    private List<Pod> pods = new ArrayList<>();
    private List<Deployment> deployments = new ArrayList<>();
    private List<Job> jobs = new ArrayList<>();
    private List<Cronjob> cronJobs = new ArrayList<>();
    private List<DaemonSet> daemonSets = new ArrayList<>();
    private List<StatefulSet> statefulSets = new ArrayList<>();

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Deployment {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DaemonSet {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Job {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Cronjob {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatefulSet {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pod {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NodeMetrics {
        private String node;
        private long cpuMilliCoresUsage;
        private long memoryMegabyteUsage;
        private long cpuMilliCoresCapacity;
        private long memoryMegabyteCapacity;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PodMetrics {
        private String namespace;
        private String pod;
        private long cpuMilliCoresUsage;
        private long memoryMegabyteUsage;
    }
}
