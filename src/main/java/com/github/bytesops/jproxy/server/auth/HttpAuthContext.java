package com.github.bytesops.jproxy.server.auth;

import com.github.bytesops.jproxy.server.auth.model.HttpToken;
import com.github.bytesops.jproxy.server.context.HttpContext;
import io.netty.channel.Channel;

/**
 *
 */
public class HttpAuthContext {

    private static final String AUTH_KEY = "http_auth";

    public static HttpToken getToken(Channel clientChanel) {
        return HttpContext.get(clientChanel, AUTH_KEY);
    }

    public static void setToken(Channel clientChanel, HttpToken httpToken) {
        HttpContext.set(clientChanel, AUTH_KEY, httpToken);
    }
}
