package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.server.HttpProxyServer;

/**
 *
 */
public class HttpProxyServerApp {
    public static void main(String[] args) {
        System.out.println("start proxy server");
        int port = 9999;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new HttpProxyServer().start(port);
    }
}
