package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
public class Resources{

    private Limits limits;
    private Requests requests;

    @Builder
    @Data
    public static class Limits{
        private String cpu;
        private String memory;
    }

    @Builder
    @Data
    public static class Requests{
        private String cpu;
        private String memory;
    }

}
