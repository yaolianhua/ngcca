package io.hotcloud.common;

import java.util.function.Supplier;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class Assert {
    private Assert() {
    }

    public static void hasText(String text, String message, int code) {
        if (text == null || text.trim().isEmpty()) {
            throw new HotCloudException(message, code);
        }
    }

    public static void hasText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            throw new HotCloudException(message);
        }
    }

    public static void notNull(Object object, Supplier<String> message) {
        notNull(object, message.get(), 400);
    }

    public static void notNull(Object object, String message) {
        notNull(object, message, 400);
    }

    public static void notNull(Object object, String message, int code) {
        if (null == object) {
            throw new HotCloudException(message, code);
        }
    }

    public static void argument(boolean expression, Supplier<String> message) {
        argument(expression, message.get());
    }

    public static void argument(boolean expression, String message) {
        if (!expression) {
            throw new HotCloudException(message, 400);
        }
    }

    public static void state(boolean expression, Supplier<String> message) {
        state(expression, message.get(), 403);
    }

    public static void state(boolean expression, String message, int code) {
        if (!expression) {
            throw new HotCloudException(message, code);
        }
    }
}
