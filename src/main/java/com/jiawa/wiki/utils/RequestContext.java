package com.jiawa.wiki.utils;


import java.io.Serializable;

public class RequestContext implements Serializable {

    /**
     * 使用ThreadLocal存本机Ip
     */
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}
