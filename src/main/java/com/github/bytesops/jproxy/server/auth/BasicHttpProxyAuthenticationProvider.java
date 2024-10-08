package com.github.bytesops.jproxy.server.auth;

import com.github.bytesops.jproxy.server.auth.model.BasicHttpToken;

import java.util.Base64;

/**
 *
 */
public abstract class BasicHttpProxyAuthenticationProvider implements HttpProxyAuthenticationProvider<BasicHttpToken> {

    public static final String AUTH_TYPE_BASIC = "Basic";
    public static final String AUTH_REALM_BASIC = "Access to the staging site";

    public String authType() {
        return AUTH_TYPE_BASIC;
    }

    public String authRealm() {
        return AUTH_REALM_BASIC;
    }

    protected abstract BasicHttpToken authenticate(String usr, String pwd);

    public BasicHttpToken authenticate(String authorization) {
        String usr = "";
        String pwd = "";
        if (authorization != null && authorization.length() > 0) {
            String token = authorization.substring(AUTH_TYPE_BASIC.length() + 1);
            String decode = new String(Base64.getDecoder().decode(token));
            String[] arr = decode.split(":");
            if (arr.length >= 1) {
                usr = arr[0];
            }
            if (arr.length >= 2) {
                pwd = arr[1];
            }
        }
        return authenticate(usr, pwd);
    }

}
