package com.github.bytesops.jproxy.server.accept;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 *
 */
public interface HttpProxyAcceptHandler {
    /**
     * 客户端有新的连接建立时触发
     *
     * @param request request
     * @param clientChannel clientChannel
     * @return 返回true表示放行，返回false则断开连接
     */
    boolean onAccept(HttpRequest request, Channel clientChannel);

    /**
     * 客户端连接关闭时触发
     *
     * @param clientChannel clientChannel
     */
    void onClose(Channel clientChannel);
}
