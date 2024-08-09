package com.github.bytesops.jproxy;

import com.github.bytesops.jproxy.intercept.HttpProxyIntercept;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptInitializer;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import com.github.bytesops.jproxy.util.ProtoUtil;
import com.github.bytesops.jproxy.server.HttpProxyServer;
import com.github.bytesops.jproxy.server.HttpProxyServerConfig;
import com.github.bytesops.jproxy.server.auth.BasicHttpProxyAuthenticationProvider;
import com.github.bytesops.jproxy.server.auth.HttpAuthContext;
import com.github.bytesops.jproxy.server.auth.model.BasicHttpToken;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * 认证
 */
public class AuthHttpProxyServer {

    // curl -i -x 127.0.0.1:9999 -U admin:123456 https://www.baidu.com
    // curl -v http://127.0.0.1:9999/status/health
    public static void main(String[] args) throws Exception {
        HttpProxyServerConfig config = new HttpProxyServerConfig();
        config.setAuthenticationProvider(new BasicHttpProxyAuthenticationProvider() {
            @Override
            protected BasicHttpToken authenticate(String usr, String pwd) {
                if ("admin".equals(usr) && "123456".equals(pwd)) {
                    return new BasicHttpToken(usr, pwd);
                }
                return null;
            }

            @Override
            public boolean matches(HttpRequest request) {
                if (request.uri().matches("^/status/health$")) {
                    return false;
                }
                return true;
            }
        });
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new HttpProxyIntercept() {
                            private boolean isDirect = false;

                            @Override
                            public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, HttpProxyInterceptPipeline pipeline) throws Exception {
                                ProtoUtil.RequestProto requestProto = pipeline.getRequestProto();
                                if (!requestProto.getProxy() && httpRequest.uri().matches("^/status/health$")) {
                                    isDirect = true;

                                    HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK);

                                    String res = "OK";
                                    byte[] bts = res.getBytes(StandardCharsets.UTF_8);

                                    httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
                                    httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, bts.length);
                                    httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                                    HttpContent httpContent = new DefaultLastHttpContent();
                                    httpContent.content().writeBytes(bts);
                                    clientChannel.writeAndFlush(httpResponse);
                                    clientChannel.writeAndFlush(httpContent);
                                    clientChannel.close();
                                }

                            }

                            @Override
                            public void beforeRequest(Channel clientChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
                                if (!isDirect) pipeline.beforeRequest(clientChannel, httpContent);
                            }
                        });


                        pipeline.addLast(new HttpProxyIntercept() {
                            @Override
                            public void beforeConnect(Channel clientChannel, HttpProxyInterceptPipeline pipeline) throws Exception {
                                System.out.println(HttpAuthContext.getToken(clientChannel));
                            }
                        });
                    }
                })
                .start(9999);
    }
}
