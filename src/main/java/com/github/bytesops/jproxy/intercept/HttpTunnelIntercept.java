package com.github.bytesops.jproxy.intercept;

import com.github.bytesops.jproxy.util.ProtoUtil;

/**
 * 用于拦截隧道请求，在代理服务器与目标服务器连接前
 */
public interface HttpTunnelIntercept {
    void handle(ProtoUtil.RequestProto requestProto);
}
