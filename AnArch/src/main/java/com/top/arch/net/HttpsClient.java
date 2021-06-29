package com.top.arch.net;

public class HttpsClient extends HttpEvent implements HttpManager {

    private volatile static HttpsClient httpClient = null;
    private static final Object lock = new Object();
    private HttpsClient() {}

    //单例模式
    public static HttpManager getInstance() {
        if (httpClient == null) {
            synchronized (lock) {
                if (httpClient == null) {
                    httpClient = new HttpsClient();
                }
            }
        }
        return httpClient;
    }

    @Override
    public <T> void Post(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.POST);
        httpRequest(param, callback);
    }

    @Override
    public <T> void Get(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.GET);
        httpRequest(param, callback);
    }

    @Override
    public <T> void Put(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.PUT);
        httpRequest(param, callback);
    }
}
