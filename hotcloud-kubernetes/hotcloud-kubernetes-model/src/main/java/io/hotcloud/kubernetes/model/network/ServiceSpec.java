package io.hotcloud.kubernetes.model.network;

import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface ServiceSpec {

    List<ServicePort> ports();

    Map<String, String> selector();

    String sessionAffinity();

    String type();

    enum Type {
        //
        ExternalName,
        ClusterIP,
        NodePort,
        LoadBalancer
    }

    enum SessionAffinity {
        //
        ClientIP,
        None
    }
}
