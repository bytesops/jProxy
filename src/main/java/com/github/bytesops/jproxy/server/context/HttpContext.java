package com.github.bytesops.jproxy.server.context;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 *
 */
public class HttpContext {

    public static <T> T get(Channel channel, String key) {
        return channel.attr(AttributeKey.<T>valueOf(key)).get();
    }

    public static <T> void set(Channel channel, String key, T value) {
        channel.attr(AttributeKey.<T>valueOf(key)).set(value);
    }
}
