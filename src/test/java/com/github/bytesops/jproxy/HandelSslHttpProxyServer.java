package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;

public class HandelSslHttpProxyServer {

    public static void main(String[] args) throws Exception {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        config.setMaxHeaderSize(8192 * 2);
        new HttpProxyServer()
                .serverConfig(config)
                .start(9999);
    }
}
