package com.github.bytesops.jproxy.server;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface HttpProxyCACertFactory {

    X509Certificate getCACert() throws Exception;

    PrivateKey getCAPriKey() throws Exception;
}
