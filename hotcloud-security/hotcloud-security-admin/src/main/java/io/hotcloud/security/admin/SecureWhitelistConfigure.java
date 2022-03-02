package io.hotcloud.security.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@ConfigurationProperties("security.ignored")
public class SecureWhitelistConfigure {

    private List<String> urls = new LinkedList<>();
}
