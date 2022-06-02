package io.hotcloud.security.server.configure;

import io.hotcloud.common.api.Log;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties("security.ignored")
public class SecureWhitelistProperties {

    private List<String> urls = new LinkedList<>();

    @PostConstruct
    public void print() {
        Log.info(SecureWhitelistProperties.class.getName(), String.format("【Load SecureWhitelist Properties】ignored urls %s", urls));
    }
}
