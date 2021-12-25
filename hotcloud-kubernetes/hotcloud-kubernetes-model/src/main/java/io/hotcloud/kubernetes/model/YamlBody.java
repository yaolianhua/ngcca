package io.hotcloud.kubernetes.model;

import io.hotcloud.Assert;
import org.springframework.util.StringUtils;

/**
 * @author yaolianhua789@gmail.com
 **/
public class YamlBody {

    private String yaml;

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

    public static YamlBody of(String yaml) {
        Assert.argument(StringUtils.hasText(yaml), () -> "string yaml is null");
        YamlBody yamlBody = new YamlBody();
        yamlBody.setYaml(yaml);

        return yamlBody;
    }
}
