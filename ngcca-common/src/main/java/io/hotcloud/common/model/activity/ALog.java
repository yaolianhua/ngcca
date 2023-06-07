package io.hotcloud.common.model.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ALog {

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
     * activity target {@link Target}
     */
    private String target;
    /**
     * activity action {@link Action}
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
