package io.hotcloud.security.admin.configure;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties("security.ignored")
@Slf4j
public class SecureWhitelistConfigure {

    private List<String> urls = new LinkedList<>();

    @PostConstruct
    public void print() {
        log.info("【Load SecureWhitelist Configuration】ignored urls {}", urls);
    }
}
