package io.hotcloud.kubernetes.model;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Strategy {

    private Type type = Type.RollingUpdate;
    private RollingUpdate rollingUpdate = new RollingUpdate();

    public Strategy(Type type, RollingUpdate rollingUpdate) {
        this.type = type;
        this.rollingUpdate = rollingUpdate;
    }

    public Strategy() {
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
