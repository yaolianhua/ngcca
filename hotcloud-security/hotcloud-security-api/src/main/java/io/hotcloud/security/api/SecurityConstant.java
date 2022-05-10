package io.hotcloud.security.api;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class SecurityConstant {

    public static final String CACHE_USER_KEY_PREFIX = "hotcloud:user:%s";
    public static final String CACHE_NAMESPACE_USER_KEY_PREFIX = "hotcloud:namespace:user:%s";
    public static final String CACHE_USERS_KEY_PREFIX = "hotcloud:user:all";
    public static final String QUEUE_APPLICATION_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE = "hotcloud.message.security.user.delete.application.subscribe";
    public static final String QUEUE_BUILDPACK_SUBSCRIBE_SECURITY_USER_DELETE_MESSAGE = "hotcloud.message.security.user.delete.buildpack.subscribe";
    public static final String EXCHANGE_FANOUT_SECURITY_MESSAGE = "hotcloud.message.security.broadcast";
}
