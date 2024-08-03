package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.exception.HttpProxyExceptionHandle;
import com.github.bytesops.jproxy.intercept.HttpProxyIntercept;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptInitializer;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;
import com.github.bytesops.jproxy.util.HttpUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

/**
 *
 */
public class InterceptForwardHttpProxyServer {


    // curl -k -x 127.0.0.1:9999 https://www.baidu.com

    public static void main(String[] args) throws Exception {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new HttpProxyIntercept() {
                            @Override
                            public void beforeRequest(Channel clientChannel, HttpRequest httpRequest,
                                                      HttpProxyInterceptPipeline pipeline) throws Exception {
                                //匹配到百度的请求转发到淘宝
                                if (HttpUtil.checkUrl(httpRequest, "^www.baidu.com$")) {
                                    pipeline.getRequestProto().setHost("www.taobao.com");
                                    pipeline.getRequestProto().setPort(443);
                                    pipeline.getRequestProto().setSsl(true);
                                    httpRequest.headers().set(HttpHeaderNames.HOST, "www.taobao.com");
                                }
                                pipeline.beforeRequest(clientChannel, httpRequest);
                            }
                        });
                    }
                })
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
