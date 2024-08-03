package com.github.bytesops.jproxy.server.accept;

import com.github.bytesops.jproxy.util.ProtoUtil;

import java.util.List;

/**
 * 通过域名配置是否走中间人攻击
 */
public class DomainHttpProxyMitmMatcher implements HttpProxyMitmMatcher {

    private List<String> domains;

    public DomainHttpProxyMitmMatcher(List<String> domains) {
        this.domains = domains;
    }

    @Override
    public boolean doMatch(ProtoUtil.RequestProto requestProto) {
        if (domains == null || domains.isEmpty()) {
            return false;
        }
        return domains.stream().anyMatch(host -> requestProto.getHost().equals(host));
    }
}
