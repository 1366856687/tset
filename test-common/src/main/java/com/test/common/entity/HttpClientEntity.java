package com.test.common.entity;

/**
 * @program: rms-admin
 * @description: httpClient连接池配置类
 * @author: RZW
 * @create: 2021-08-03 11:48
 **/

public class HttpClientEntity {

    /**
     * 最大连接数
     */
    private static int maxTotalConnections = 10;

    /**
     * 每个路由最大连接数
     */
    private static int maxRouteConnections = 5;

    /**
     * 客户端从连接池中获取连接的超时时间
     */
    private static int waitTimeout = 60000;

    /**
     * 客户端与服务器建立连接的超时时间
     */
    private static int connectTimeout = 60000;

    /**
     * 客户端从服务端读取数据的超时时间
     */
    private static int readTimeout = 60000;

    /**
     * 重试计数
     */
    private static int retryCount = 3;

    public static int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    public  void setMaxTotalConnections(int maxTotalConnections) {
        HttpClientEntity.maxTotalConnections = maxTotalConnections;
    }

    public static int getMaxRouteConnections() {
        return maxRouteConnections;
    }

    public  void setMaxRouteConnections(int maxRouteConnections) {
        HttpClientEntity.maxRouteConnections = maxRouteConnections;
    }

    public static int getWaitTimeout() {
        return waitTimeout;
    }

    public  void setWaitTimeout(int waitTimeout) {
        HttpClientEntity.waitTimeout = waitTimeout;
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public  void setConnectTimeout(int connectTimeout) {
        HttpClientEntity.connectTimeout = connectTimeout;
    }

    public static int getReadTimeout() {
        return readTimeout;
    }

    public  void setReadTimeout(int readTimeout) {
        HttpClientEntity.readTimeout = readTimeout;
    }

    public static int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        HttpClientEntity.retryCount = retryCount;
    }

}
