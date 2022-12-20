package io.hotcloud.common.model.registry.dockerregistry;

import lombok.Data;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DockerRegistryManifest {

    private String name;
    private Integer schemaVersion;
    private String tag;
    private String architecture;
    private String digest;
    private List<FsLayer> fsLayers;
    private List<History> history;
    private List<DockerRegistryManifestJws> signatures;

    @Data
    public static class FsLayer {
        private String blobSum;
    }

    @Data
    public static class History {
        private String v1Compatibility;
    }

    @Data
    public static class DockerRegistryManifestJws {

        private Header header;
        private String signature;

        @Data
        public static class Jwk {
            private String crv;
            private String kid;
            private String kty;
            private String x;
            private String y;
        }

        @Data
        public static class Header {
            private Jwk jwk;
            private String alg;
        }
    }

}
