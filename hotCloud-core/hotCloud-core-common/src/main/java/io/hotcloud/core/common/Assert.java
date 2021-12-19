package io.hotcloud.core.common;

import java.util.function.Supplier;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class Assert {
    private Assert() {
    }

    public static void notNull(Object object, Supplier<String> message) {
        if (null == object) {
            throw new HotCloudException(message.get(), 400);
        }
    }

    public static void notNull(Object object, String message) {
        if (null == object) {
            throw new HotCloudException(message, 400);
        }
    }

    public static void argument(boolean expression, Supplier<String> message) {
        if (!expression) {
            throw new HotCloudException(message.get(), 400);
        }
    }

    public static void argument(boolean expression, String message) {
        if (!expression) {
            throw new HotCloudException(message, 400);
        }
    }

    public static void state(boolean expression, Supplier<String> message) {
        if (!expression) {
            throw new HotCloudException(message.get(), 400);
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new HotCloudException(message, 400);
        }
    }
}
