package io.hotCloud.core.kubernetes.container;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Resources{

    private Limits limits;
    private Requests requests;

    @Data
    public static class Limits{
        private String cpu;
        private String memory;
    }

    @Data
    public static class Requests{
        private String cpu;
        private String memory;
    }

}
