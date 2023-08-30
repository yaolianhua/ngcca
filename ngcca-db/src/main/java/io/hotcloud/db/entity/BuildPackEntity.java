package io.hotcloud.db.entity;

import io.hotcloud.db.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document(collection = "buildpacks")
@Getter
@Setter
public class BuildPackEntity extends AbstractEntity {

    @Indexed(unique = true)
    private String uuid;

    @Indexed(unique = false)
    private String user;

    private String httpGitUrl;
    private String gitBranch;

    private String job;
    private String storage;
    private String secret;

    private String yaml;

    private boolean done;
    private boolean deleted;
    private String message;

    private String logs;
    private String artifact;
    private String packageUrl;
}
