package com.github.bytesops.jproxy.server.auth.model;

/**
 *
 */
public class BasicHttpToken implements HttpToken {
    private String usr;
    private String pwd;

    public BasicHttpToken() {
    }

    public BasicHttpToken(String usr, String pwd) {
        this.usr = usr;
        this.pwd = pwd;
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "HttpBasicToken{" +
                "usr='" + usr + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
