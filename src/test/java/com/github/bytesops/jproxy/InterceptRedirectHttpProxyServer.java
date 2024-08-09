package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.exception.HttpProxyExceptionHandle;
import com.github.bytesops.jproxy.intercept.HttpProxyIntercept;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptInitializer;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import com.github.bytesops.jproxy.util.HttpUtil;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

/**
 * 匹配到百度首页时重定向到指定url
 */
public class InterceptRedirectHttpProxyServer {
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
                                //匹配到百度首页跳转到淘宝
                                if (HttpUtil.checkUrl(pipeline.getHttpRequest(), "^www.baidu.com$")) {
                                    HttpResponse hookResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                    hookResponse.setStatus(HttpResponseStatus.FOUND);
                                    hookResponse.headers().set(HttpHeaderNames.LOCATION, "http://www.taobao.com");
                                    clientChannel.writeAndFlush(hookResponse);
                                    HttpContent lastContent = new DefaultLastHttpContent();
                                    clientChannel.writeAndFlush(lastContent);
                                    return;
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
