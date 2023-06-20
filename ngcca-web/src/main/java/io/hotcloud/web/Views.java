package io.hotcloud.web;

public final class Views {
    public static final String LOGIN = "login";

    public static final String TEMPLATE_DEFINITION_LIST_FRAGMENT = "admin/template-definition/template-list::content";
    public static final String TEMPLATE_DEFINITION_MANAGE = "admin/template-definition/template-manage";
    public static final String TEMPLATE_LIST = "template/template-list";
    public static final String USER_TEMPLATE_INSTANCE = "template/user-template-instance";
    public static final String USER_TEMPLATE_INSTANCE_LIST_FRAGMENT = "template/user-template-instance-list::content";
    public static final String USER_TEMPLATE_INSTANCE_DETAIL_FRAGMENT = "template/user-template-instance-detail::content";
    public static final String K8S_NODE_LIST = "admin/k8s/node-list";
    public static final String K8S_POD_LIST = "admin/k8s/pod-list";
    public static final String USER_MANAGE_LIST_FRAGMENT = "admin/user-manage/user-list::content";
    public static final String USER_MANAGE_EDIT_FRAGMENT = "admin/user-manage/user-edit::content";
    public static final String USER_MANAGE_DETAIL_FRAGMENT = "admin/user-manage/user-detail::content";
    public static final String USER_MANAGE = "admin/user-manage/user-manage";
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
