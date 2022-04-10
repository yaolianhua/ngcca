package io.hotcloud.db.core.buildpack;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "buildpacks")
@RedisHash("buildpacks")
@Getter
@Setter
public class BuildPackEntity extends AbstractEntity {

    @Indexed(unique = false)
    @org.springframework.data.redis.core.index.Indexed
    private String user;
    @Indexed(unique = false)
    @org.springframework.data.redis.core.index.Indexed
    private String clonedId;

    private String job;
    private String storage;
    private String secret;

    private String yaml;

    private boolean done;
    private String message;


}
