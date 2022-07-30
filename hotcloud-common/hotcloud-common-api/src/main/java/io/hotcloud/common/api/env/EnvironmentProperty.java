package io.hotcloud.common.api.env;

import lombok.Data;
import org.springframework.util.Assert;

import java.util.Objects;

@Data
public class EnvironmentProperty {

    private final String name;
    private final String value;
    private final  boolean system;
    public EnvironmentProperty(String name, String value, boolean system) {
        this.name = name;
        this.value = value;
        this.system = system;
    }

    public static EnvironmentProperty of(String name, String value, boolean system) {
        Assert.hasText(name, "Property name is null");

        return new EnvironmentProperty(name, value, system);
    }

    public boolean matches(String property){
        return Objects.equals(property, name);
    }

    public String getProperty(String property) {
        if (matches(property)){
            return value;
        }

        return null;
    }
}
