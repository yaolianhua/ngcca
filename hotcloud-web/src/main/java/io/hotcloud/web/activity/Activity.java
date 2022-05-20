package io.hotcloud.web.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private String user;

    private String namespace;

    private String target;

    private String action;

    private String description;

    private String targetId;

    private String targetName;
}
