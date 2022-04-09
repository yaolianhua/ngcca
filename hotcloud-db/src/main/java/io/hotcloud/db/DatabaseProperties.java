package io.hotcloud.db;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaolianhua789@gmail.com
 **/
@ConfigurationProperties(prefix = "db")
@Data
public class DatabaseProperties {

    public static final String PROPERTIES_TYPE_NAME = "db.type";
    private Type type = Type.redis;

    private MongodbProperties mongodb;
    private RedisProperties redis;

    public enum Type {
        //
        redis,
        mongodb
    }

    @Data
    public static class MongodbProperties {
        private String database = "hotcloud";
        private String username;
        private String password;
        private String host;
        private Integer port = 27017;
    }

    @Data
    public static class RedisProperties {

        private String host;
        private Integer port = 6379;
        private Integer database = 0;

        private String password;

    }
}
