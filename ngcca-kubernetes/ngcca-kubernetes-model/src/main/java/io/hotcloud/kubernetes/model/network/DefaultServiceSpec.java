package io.hotcloud.kubernetes.model.network;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class DefaultServiceSpec implements ServiceSpec {

    @NotEmpty(message = "service ports is empty")
    private List<ServicePort> ports = new ArrayList<>();

    private Map<String, String> selector = new HashMap<>();

    private SessionAffinity sessionAffinity = SessionAffinity.None;

    private Type type = Type.ClusterIP;

    @Override
    public List<ServicePort> ports() {
        return ports;
    }

    @Override
    public Map<String, String> selector() {
        return selector;
    }

    @Override
    public String sessionAffinity() {
        return sessionAffinity.name();
    }

    @Override
    public String type() {
        return type.name();
    }
}
