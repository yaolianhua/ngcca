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

    private String icon;

    private String display;

    public String getDisplay() {
        if ("Git_Clone".equalsIgnoreCase(target)) {
            if ("Delete".equals(action)) {
                return "【删除】代码仓库";
            }
            return "【新建】代码仓库";
        }
        if ("BuildPack".equalsIgnoreCase(target)) {
            if ("Delete".equals(action)) {
                return "【删除】构建面板";
            }
            return "【新建】构建面板";
        }
        if ("Instance_Template".equalsIgnoreCase(target)) {
            if ("Delete".equals(action)) {
                return "【删除】模板实例";
            }
            return "【新建】模板实例";
        }
        if ("Application".equalsIgnoreCase(target)) {
            if ("Delete".equals(action)) {
                return "【删除】应用实例";
            }
            return "【新建】应用实例";
        }
        return "";
    }

    public String getIcon() {
        if ("Git_Clone".equalsIgnoreCase(target)) {
            return "fa-code-branch";
        }
        if ("BuildPack".equalsIgnoreCase(target)) {
            return "fa-wrench";
        }
        if ("Instance_Template".equalsIgnoreCase(target)) {
            return "fa-cubes";
        }
        if ("Application".equalsIgnoreCase(target)) {
            return "fa-cloud";
        }
        return "fa-circle";
    }
}
