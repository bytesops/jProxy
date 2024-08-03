package com.github.bytesops.jproxy.server.auth;

import com.github.bytesops.jproxy.server.auth.model.HttpToken;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

/**
 *
 */
public interface HttpProxyAuthenticationProvider<R extends HttpToken> {
    String authType();

    String authRealm();

    R authenticate(String authorization);

    default R authenticate(HttpRequest request) {
        return authenticate(request.headers().get(HttpHeaderNames.PROXY_AUTHORIZATION));
    }

    default boolean matches(HttpRequest request) {
        return true;
    }
}
