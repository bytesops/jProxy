package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.intercept.HttpProxyInterceptInitializer;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import com.github.bytesops.jproxy.intercept.common.FullResponseIntercept;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;
import com.github.bytesops.jproxy.util.HttpUtil;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * 测试 main
 */
public class HttpProxyServerApp {

    public static void main(String[] args) {

        System.out.println("start proxy server");

        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new FullResponseIntercept() {

                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                // 在匹配到百度首页时插入js
                                return HttpUtil.checkUrl(pipeline.getHttpRequest(), "^www.baidu.com$")
                                        && HttpUtil.isHtml(httpRequest, httpResponse);
                            }

                            @Override
                            public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                // 打印匹配到的 host
                                String host = httpRequest.headers().get(HttpHeaderNames.HOST);
                                System.out.println(host);
                                // 修改响应头和响应体
                                httpResponse.headers().set("handle", "edit head");
                                httpResponse.content().writeBytes("<script>alert('hello jproxy')</script>".getBytes());
                            }
                        });
                    }
                })
                .start(9999);
    }
}
