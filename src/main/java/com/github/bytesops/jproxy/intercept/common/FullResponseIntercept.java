package com.github.bytesops.jproxy.intercept.common;

import com.github.bytesops.jproxy.intercept.HttpProxyIntercept;
import com.github.bytesops.jproxy.intercept.HttpProxyInterceptPipeline;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

public abstract class FullResponseIntercept extends HttpProxyIntercept {

    /**
     * default max content length size is 8MB
     */
    private static final int DEFAULT_MAX_CONTENT_LENGTH = 1024 * 1024 * 8;

    private int maxContentLength;

    private Boolean isMatch;

    public FullResponseIntercept() {
        this(DEFAULT_MAX_CONTENT_LENGTH);
    }

    public FullResponseIntercept(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel,
                                    HttpResponse httpResponse,
                                    HttpProxyInterceptPipeline pipeline) throws Exception {
        if (httpResponse instanceof FullHttpResponse) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) httpResponse;
            // 判断是第一个处理FullResponse的拦截器是否匹配
            boolean isFirstMatch = isMatch != null && isMatch == true;
            // 判断后续的拦截器是否匹配
            boolean isAfterMatch = isFirstMatch ? false : match(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline);
            if (isFirstMatch || isAfterMatch) {
                handleResponse(pipeline.getHttpRequest(), fullHttpResponse, pipeline);
                if (fullHttpResponse.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                    httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
                }
                if (pipeline.getHttpRequest() instanceof FullHttpRequest) {
                    FullHttpRequest fullHttpRequest = (FullHttpRequest) pipeline.getHttpRequest();
                    if (fullHttpRequest.content().refCnt() > 0) {
                        ReferenceCountUtil.release(fullHttpRequest);
                    }
                }
            }
            if (isFirstMatch) {
                proxyChannel.pipeline().remove("decompress");
                proxyChannel.pipeline().remove("aggregator");
            }
        } else {
            this.isMatch = match(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline);
            if (this.isMatch) {
                proxyChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
                proxyChannel.pipeline()
                        .addAfter("decompress", "aggregator", new HttpObjectAggregator(maxContentLength));
                proxyChannel.pipeline().fireChannelRead(httpResponse);
                return;
            }
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }

    /**
     * 匹配到的响应会解码成FullResponse
     */
    public abstract boolean match(HttpRequest httpRequest, HttpResponse httpResponse,
                                  HttpProxyInterceptPipeline pipeline);

    /**
     * 拦截并处理响应
     */
    public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse,
                               HttpProxyInterceptPipeline pipeline) {
    }
}
