package io.hotcloud.db.core.buildpack;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "repository_cloned")
@CompoundIndex(
        name = "user_project_idx",
        def = "{'user': 1, 'project': 1}",
        unique = true
)
@RedisHash("repository_cloned")
@Getter
@Setter
public class GitClonedEntity extends AbstractEntity {

    @org.springframework.data.redis.core.index.Indexed
    private String user;

    private String url;
    private String localPath;
    @org.springframework.data.redis.core.index.Indexed
    private String project;
    private String branch;
    private boolean success;
    private boolean force;
    private String username;
    private String password;

    private String error;
}
