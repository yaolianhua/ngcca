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

    //for web

    private String icon;

    private String display;

    public String getDisplay() {
        if (Target.Git_Clone.name().equalsIgnoreCase(target)) {
            if (Action.Delete.name().equals(action)) {
                return "【删除】代码仓库";
            }
            return "【新建】代码仓库";
        }
        if (Target.BuildPack.name().equalsIgnoreCase(target)) {
            if (Action.Delete.name().equals(action)) {
                return "【删除】构建面板";
            }
            return "【新建】构建面板";
        }
        if (Target.Instance_Template.name().equalsIgnoreCase(target)) {
            if (Action.Delete.name().equals(action)) {
                return "【删除】模板实例";
            }
            return "【新建】模板实例";
        }
        if (Target.Application.name().equalsIgnoreCase(target)) {
            if (Action.Delete.name().equals(action)) {
                return "【删除】应用实例";
            }
            return "【新建】应用实例";
        }
        return "";
    }

    public String getIcon() {
        if (Target.Git_Clone.name().equalsIgnoreCase(target)) {
            return "fa-code-branch";
        }
        if (Target.BuildPack.name().equalsIgnoreCase(target)) {
            return "fa-wrench";
        }
        if (Target.Instance_Template.name().equalsIgnoreCase(target)) {
            return "fa-cubes";
        }
        if (Target.Application.name().equalsIgnoreCase(target)) {
            return "fa-cloud";
        }
        return "fa-circle";
    }

    public enum Target {
        //
        Git_Clone,
        BuildPack,
        Instance_Template,
        Application
    }

    public enum Action {
        //
        Create,
        Update,
        Delete
    }
}
