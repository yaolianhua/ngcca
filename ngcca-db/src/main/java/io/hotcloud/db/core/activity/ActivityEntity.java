package io.hotcloud.db.core.activity;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author yaolianhua789@gmail.com
 **/
@Document("activities")
@Getter
@Setter
public class ActivityEntity extends AbstractEntity {

    @Indexed
    private String user;
    private String namespace;
    @Indexed
    private String target;
    @Indexed
    private String action;
    private String description;
    @Indexed
    private String targetId;
    private String targetName;

}
