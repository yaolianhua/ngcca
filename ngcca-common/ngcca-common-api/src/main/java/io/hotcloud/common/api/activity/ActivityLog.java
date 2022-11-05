package io.hotcloud.common.api.activity;

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
public class ActivityLog {

    private String id;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    /**
     * activity user
     */
    private String user;
    /**
     * activity kubernetes namespace. it can be {@code null}
     */
    private String namespace;
    /**
     * activity target {@link ActivityTarget}
     */
    private String target;
    /**
     * activity action {@link ActivityAction}
     */
    private String action;
    /**
     * activity content
     */
    private String description;
    /**
     * activity target id
     */
    private String targetId;
    /**
     * activity target name
     */
    private String targetName;
}
