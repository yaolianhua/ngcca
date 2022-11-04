package io.hotcloud.db.core.activity;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document("activities")
@RedisHash("activities")
@Getter
@Setter
public class ActivityEntity extends AbstractEntity {

    @Indexed
    @org.springframework.data.redis.core.index.Indexed
    private String user;
    private String namespace;
    @Indexed
    @org.springframework.data.redis.core.index.Indexed
    private String target;
    @Indexed
    @org.springframework.data.redis.core.index.Indexed
    private String action;
    private String description;
    @Indexed
    @org.springframework.data.redis.core.index.Indexed
    private String targetId;
    private String targetName;

}
