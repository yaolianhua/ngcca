package io.hotcloud.server.env;

import lombok.Data;
import org.springframework.util.Assert;

import java.util.Objects;

@Data
public class EnvironmentProperty {

    private final String name;
    private final Object value;
    private final boolean system;

    public EnvironmentProperty(String name, Object value, boolean system) {
        this.name = name;
        this.value = value;
        this.system = system;
    }

    public static EnvironmentProperty of(String name, Object value, boolean system) {
        Assert.hasText(name, "Property name is null");

        return new EnvironmentProperty(name, value, system);
    }

    public boolean matches(String property){
        return Objects.equals(property, name);
    }

    public Object getProperty(String property) {
        if (matches(property)) {
            return value;
        }

        return null;
    }
}
