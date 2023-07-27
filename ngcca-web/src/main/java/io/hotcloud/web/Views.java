package io.hotcloud.web;

public final class Views {
    public static final String LOGIN = "login";
    public static final String TEMPLATE_LIST = "template/template-list";
    public static final String USER_TEMPLATE_INSTANCE = "template/user-template-instance";
    public static final String USER_TEMPLATE_INSTANCE_LIST_FRAGMENT = "template/user-template-instance-list::content";
    public static final String K8S_NODE_LIST = "admin/cluster/node-list";
    public static final String K8S_POD_LIST = "admin/cluster/pod-list";
    public static final String REGISTRY_IMAGE_LIST = "admin/system/registryimage-list";
    public static final String SYSTEM_ENVIRONMENT_LIST = "admin/system/system-environment-list";
    public static final String APP_ENVIRONMENT_LIST = "admin/system/app-environment-list";
    public static final String ADMIN_ACTIVITY_LIST = "admin/system/activity-list";
    public static final String USER_MANAGE_LIST_FRAGMENT = "admin/system/user-manage/user-list::content";
    public static final String USER_MANAGE_EDIT_FRAGMENT = "admin/system/user-manage/user-edit::content";
    public static final String USER_MANAGE_DETAIL_FRAGMENT = "admin/system/user-manage/user-detail::content";
    public static final String USER_MANAGE = "admin/system/user-manage/user-manage";
    public static final String TEMPLATE_DEFINITION_LIST_FRAGMENT = "admin/system/template-definition/template-list::content";
    public static final String TEMPLATE_DEFINITION_MANAGE = "admin/system/template-definition/template-manage";
    public static final String ADMIN_LOGIN = "admin/login";
    public static final String INDEX = "index";
    public static final String REDIRECT_ADMIN_LOGIN = "redirect:/administrator/login";
    public static final String ADMIN_INDEX = "admin/index";
    public static final String REDIRECT_ADMIN_INDEX = "redirect:/administrator/index";
    public static final String REDIRECT_INDEX = "redirect:/index";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String PREFIX_ADMIN = "/administrator";

    private Views() {
    }

}
