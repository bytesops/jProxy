package com.github.bytesops.jproxy.server.accept;

import com.github.bytesops.jproxy.util.ProtoUtil;

/**
 * 用于匹配请求是否需要走中间人攻击
 */
public interface HttpProxyMitmMatcher {
    /**
     * 客户端有新的连接建立时触发
     *
     * @param requestProto requestProto
     * @return 返回true表示走中间人攻击，返回false则直接转发
     */
    boolean doMatch(ProtoUtil.RequestProto requestProto);
}
