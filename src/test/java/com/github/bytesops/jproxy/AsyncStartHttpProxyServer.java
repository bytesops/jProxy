package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;

/**
 * 移步处理
 */
public class AsyncStartHttpProxyServer {

    public static void main(String[] args) {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setBossGroupThreads(1);
        new HttpProxyServer()
                .serverConfig(config)
                .startAsync(9999).whenComplete((result, cause) -> {
            if (cause != null) {
                cause.printStackTrace();
            }
        });
    }
}
