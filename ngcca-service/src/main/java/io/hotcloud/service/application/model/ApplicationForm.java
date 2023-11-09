package io.hotcloud.service.application.model;

import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.db.model.ApplicationInstanceSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationForm {

    @Builder.Default
    private String clusterId = CommonConstant.DEFAULT_CLUSTER_ID;
    @Builder.Default
    private String name = "";
    private boolean enableIngressAccess;

    @Builder.Default
    private Integer serverPort = 8080;

    @Builder.Default
    private ApplicationInstanceSource source = new ApplicationInstanceSource();

    @Builder.Default
    private Integer replicas = 1;
    @Builder.Default
    private Map<String, String> envs = new HashMap<>();

    private String envStrings;

    public Map<String, String> getEnvs() {
        Map<String, String> envMap = new HashMap<>();
        if (envStrings != null && !envStrings.isEmpty()) {
            for (String pair : envStrings.split(",")) {
                String[] kvArrays = pair.strip().split("=", 2);
                envMap.put(kvArrays[0].strip(), kvArrays[1].strip());
            }
        }

        if (!envs.isEmpty()) {
            envs.putAll(envMap);
            return envs;
        }

        return envMap;
    }
}
