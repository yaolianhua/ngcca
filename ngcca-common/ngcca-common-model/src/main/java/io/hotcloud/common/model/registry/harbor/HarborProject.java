package io.hotcloud.common.model.registry.harbor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HarborProject {

    @JsonProperty("creation_time")
    private String creationTime;

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("owner_name")
    private String ownerName;

    @JsonProperty("owner_id")
    private int ownerId;

    @JsonProperty("repo_count")
    private int repoCount;

    @JsonProperty("registry_id")
    private int registryId;

    @JsonProperty("update_time")
    private String updateTime;

    @JsonProperty("current_user_role_id")
    private int currentUserRoleId;

    @JsonProperty("deleted")
    private boolean deleted;

    @JsonProperty("project_id")
    private int projectId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("current_user_role_ids")
    private Object currentUserRoleIds;

    @JsonProperty("cve_allowlist")
    private CveAllowlist cveAllowlist;

    @JsonProperty("chart_count")
    private int chartCount;

    @Data
    public static class CveAllowlist {

        @JsonProperty("creation_time")
        private String creationTime;

        @JsonProperty("update_time")
        private String updateTime;

        @JsonProperty("project_id")
        private int projectId;

        @JsonProperty("id")
        private int id;

        @JsonProperty("items")
        private Object items;
    }

    @Data
    public static class Metadata {

        @JsonProperty("severity")
        private String severity;

        @JsonProperty("prevent_vul")
        private String preventVul;

        @JsonProperty("reuse_sys_cve_allowlist")
        private String reuseSysCveAllowlist;

        @JsonProperty("public")
        private String jsonMemberPublic;

        @JsonProperty("auto_scan")
        private String autoScan;

        @JsonProperty("enable_content_trust")
        private String enableContentTrust;

    }
}