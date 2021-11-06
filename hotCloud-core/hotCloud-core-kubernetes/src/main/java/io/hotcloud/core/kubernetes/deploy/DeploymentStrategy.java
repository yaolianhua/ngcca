package io.hotcloud.core.kubernetes.deploy;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DeploymentStrategy {

    private Type type = Type.RollingUpdate;
    private RollingUpdate rollingUpdate = new RollingUpdate();

    public DeploymentStrategy(Type type, RollingUpdate rollingUpdate) {
        this.type = type;
        this.rollingUpdate = rollingUpdate;
    }

    public DeploymentStrategy() {
    }

    public enum Type {
        //
        RollingUpdate, Recreate
    }

    @Data
    public static class RollingUpdate {

        private String maxSurge = "25%";
        private String maxUnavailable = "25%";
    }
}
