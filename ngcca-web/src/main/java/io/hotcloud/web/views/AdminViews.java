package io.hotcloud.web.views;

public final class AdminViews {

    public static final String PREFIX_ADMIN = "/administrator";
    public static final String ADMIN_INDEX = "admin/index";

    private AdminViews() {
    }

    public static final class Cluster {
        public static final String CLUSTER_NODE_LIST = "admin/cluster/node-list";
        public static final String CLUSTER_NODE_LIST_FRAGMENT = "admin/cluster/node-list::content";
        public static final String CLUSTER_LIST = "admin/cluster/cluster-list";
        public static final String CLUSTER_POD_LIST = "admin/cluster/pod-list";

        private Cluster() {
        }
    }

    public static final String REGISTRY_IMAGE_LIST = "admin/system/registryimage-list";

    public static final String ADMIN_ACTIVITY_LIST = "admin/system/activity-list";

    public static final class UserManage {
        public static final String USER_MANAGE_LIST_FRAGMENT = "admin/system/user-manage/user-list::content";
        public static final String USER_MANAGE_EDIT_FRAGMENT = "admin/system/user-manage/user-edit::content";
        public static final String USER_MANAGE_DETAIL_FRAGMENT = "admin/system/user-manage/user-detail::content";
        public static final String USER_MANAGE = "admin/system/user-manage/user-manage";

        private UserManage() {
        }
    }

    public static final class TemplateDefinition {
        public static final String TEMPLATE_DEFINITION_LIST_FRAGMENT = "admin/system/template-definition/template-list::content";
        public static final String TEMPLATE_DEFINITION_MANAGE = "admin/system/template-definition/template-manage";

        private TemplateDefinition() {
        }
    }

    public static final String ADMIN_LOGIN = "admin/login";
    public static final String REDIRECT_ADMIN_LOGIN = "redirect:/administrator/login";
    public static final String REDIRECT_ADMIN_INDEX = "redirect:/administrator/index";

    public static final class Environment {
        public static final String SYSTEM_ENVIRONMENT_LIST = "admin/system/system-environment-list";
        public static final String APP_ENVIRONMENT_LIST = "admin/system/app-environment-list";

        private Environment() {
        }
    }

}
