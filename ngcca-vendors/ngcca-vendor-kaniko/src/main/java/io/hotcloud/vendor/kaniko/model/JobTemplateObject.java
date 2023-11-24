package io.hotcloud.vendor.kaniko.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JobTemplateObject {

    private String apiVersion;

    private String kind;
    private Metadata metadata;

    private Spec spec;

    @Data
    public static class Metadata {

        private Map<String, String> annotations = new HashMap<>();

        private Map<String, String> labels = new HashMap<>();

        private String name;

        private String namespace;
    }

    @Data
    public static class Spec {
        private Integer activeDeadlineSeconds;
        private Integer backoffLimit;
        private Template template;
    }

    @Data
    public static class ImagePullSecret {
        private String name;
    }

    @Data
    public static class TemplateSpec {

        private List<Containers> containers = new ArrayList<>();
        private String dnsPolicy;

        private String restartPolicy;

        private List<HostAliases> hostAliases = new ArrayList<>();

        private List<ImagePullSecret> imagePullSecrets = new ArrayList<>();


        private List<Containers> initContainers = new ArrayList<>();

        private List<Volumes> volumes;

    }

    @Data
    public static class Volumes {

        private Map<String, String> emptyDir;

        private String name;

        private Secret secret;
    }

    @Data
    public static class Secret {

        private String secretName;

        private Boolean optional;

        private List<Items> items;
    }

    @Data
    public static class Items {

        private String key;
        private String path;

    }

    @Data
    public static class Template {

        private Metadata metadata;

        private TemplateSpec spec;
    }

    @Data
    public static class Containers {
        private String name;

        private String image;
        private String imagePullPolicy;
        private List<String> args;

        private List<Object> ports;

        private List<String> command;

        private Resources resources;

        private List<VolumeMounts> volumeMounts = new ArrayList<>();
    }

    @Data
    public static class VolumeMounts {

        private String mountPath;
        private String name;
        private Boolean readOnly;
    }

    @Data
    public static class Resources {

        private Requests requests;

        private Requests limits;
    }

    @Data
    public static class Requests {

        private String memory;

        private String cpu;
    }

    @Data
    public static class HostAliases {
        private String ip;
        private List<String> hostnames;

        public HostAliases(String ip, List<String> hostnames) {
            this.ip = ip;
            this.hostnames = hostnames;
        }
    }
}