package io.hotcloud.module.db.entity;

import io.hotcloud.module.db.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "repository_cloned")
@CompoundIndex(
        name = "user_project_idx",
        def = "{'user': 1, 'project': 1}",
        unique = true
)
@Getter
@Setter
public class GitClonedEntity extends AbstractEntity {

    private String user;

    private String url;
    private String dockerfile;
    private String localPath;
    private String project;
    private String branch;
    private boolean success;
    private boolean force;
    private String username;
    private String password;

    private String error;
}
