package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Builder
@Data
public class HttpGet{
    private String host;
    private String path;
    private String port;
    @Builder.Default
    private List<HttpHeader> httpHeaders = new ArrayList<>();

    @Builder
    @Data
    public static class HttpHeader{
        private String name;
        private String value;
    }

}
