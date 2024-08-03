package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.intercept.HttpProxyIntercept;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptInitializer;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;
import com.github.bytesops.jproxy.server.accept.DomainHttpProxyMitmMatcher;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Arrays;

public class MimtMatchHttpProxyServer {

    // curl -k -x 127.0.0.1:9999 https://www.baidu.com
    // curl -k -x 127.0.0.1:9999 https://www.taobao.com
    public static void main(String[] args) throws Exception {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setHandleSsl(true);
        // 设置只有访问百度才会走中间人攻击，其它域名正常转发
        config.setMitmMatcher(new DomainHttpProxyMitmMatcher(Arrays.asList("www.baidu.com")));
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new HttpProxyIntercept() {

                            // 只有请求百度域名这里才会被调用
                            @Override
                            public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
                                System.out.println(httpRequest.toString());

                                pipeline.beforeRequest(clientChannel, httpRequest);
                            }
                        });
                    }
                })
                .start(9999);
    }
}
