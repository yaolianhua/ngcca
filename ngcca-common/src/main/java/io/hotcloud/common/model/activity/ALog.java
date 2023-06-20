package io.hotcloud.common.model.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public String getIcon() {
        if (Target.BUILDPACK.name().equalsIgnoreCase(target)) {
            return "fa-wrench";
        }
        if (Target.INSTANCE_TEMPLATE.name().equalsIgnoreCase(target)) {
            return "fa-cubes";
        }
        if (Target.APPLICATION.name().equalsIgnoreCase(target)) {
            return "fa-cloud";
        }
        return "fa-circle";
    }

    public String getDisplayName() {
        if (Objects.equals(target, Target.APPLICATION.name())) {
            if (Objects.equals(action, Action.CREATE.name())) {
                return user + " 创建应用";
            }
            if (Objects.equals(action, Action.UPDATE.name())) {
                return user + " 更新应用";
            }
            if (Objects.equals(action, Action.DELETE.name())) {
                return user + " 删除应用";
            }
        }

        if (Objects.equals(target, Target.INSTANCE_TEMPLATE.name())) {
            if (Objects.equals(action, Action.CREATE.name())) {
                return user + " 创建模板实例";
            }
            if (Objects.equals(action, Action.UPDATE.name())) {
                return user + " 更新模板实例";
            }
            if (Objects.equals(action, Action.DELETE.name())) {
                return user + " 删除模板实例";
            }
        }

        if (Objects.equals(target, Target.BUILDPACK.name())) {
            if (Objects.equals(action, Action.CREATE.name())) {
                return user + " 创建构建面板";
            }
            if (Objects.equals(action, Action.UPDATE.name())) {
                return user + " 更新构建面板";
            }
            if (Objects.equals(action, Action.DELETE.name())) {
                return user + " 删除构建面板";
            }
        }

        return "未知操作";
    }
}
