package io.hotCloud.core.kubernetes.pod.container;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class HttpGet{
    private String host;
    private String path;
    private String port;

    private List<HttpHeader> httpHeaders = new ArrayList<>();

    @Data
    public static class HttpHeader{
        private String name;
        private String value;
    }

}
