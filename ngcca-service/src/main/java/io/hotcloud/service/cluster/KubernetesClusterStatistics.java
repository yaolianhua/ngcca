package io.hotcloud.service.cluster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    private List<Service> services = new ArrayList<>();
    private List<ConfigMap> configMaps = new ArrayList<>();
    private List<Secret> secrets = new ArrayList<>();
    private List<Ingress> ingresses = new ArrayList<>();

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
    public static class Service {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConfigMap {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Secret {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Ingress {
        private String namespace;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NodeMetrics {
        private String node;
        private String ip;
        private String status;
        private String containerRuntime;
        private String kubeletVersion;
        private String architecture;
        private String osImage;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PodMetrics {
        private String namespace;
        private String pod;
        private List<Container> containers = new ArrayList<>();
        private String status;
        private RefNode refNode;
        private Set<RefService> refServices = new HashSet<>();
        private long cpuMilliCoresUsage;
        private long memoryMegabyteUsage;

        public boolean onlyOneContainer() {
            return containers.size() == 1;
        }

        public boolean existedPodService() {
            return !refServices.isEmpty();
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class RefNode {
            private String ip;
            private String name;
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
        public static class RefService {
            private String type;
            private String name;
            private String clusterIp;
            private String ports;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Container {
        private String name;
    }
}
