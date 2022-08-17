package io.hotcloud.application.api.template;

/**
 * @author yaolianhua789@gmail.com
 **/
public enum Template {
    //
    Mongodb("mongo:5.0"),
    Minio("minio/minio:latest"),
    Redis("redis:7.0"),
    RedisInsight("redislabs/redisinsight:latest"),
    Mysql("mysql:8.0"),
    Rabbitmq("rabbitmq:3.9-management");

    private final String tag;

    Template(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
