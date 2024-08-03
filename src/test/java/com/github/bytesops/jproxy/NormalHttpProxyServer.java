package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.exception.HttpProxyExceptionHandle;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import io.netty.channel.Channel;

public class NormalHttpProxyServer {

    public static void main(String[] args) throws Exception {
        new HttpProxyServer()
                .httpProxyExceptionHandle(new HttpProxyExceptionHandle() {
                    @Override
                    public void beforeCatch(Channel clientChannel, Throwable cause) throws Exception {
                        cause.printStackTrace();
                    }

                    @Override
                    public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause)
                            throws Exception {
                        cause.printStackTrace();
                    }

                })
                .start(9999);
    }
}
