package com.top.arch.interceptor;


import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 *  TODO Log拦截器代码
 */
public class LogInterceptor implements Interceptor{

    private String TAG = "LogInterceptor";
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Log.e(TAG,"==============================Interceptor Start===============================");
        Request request = chain.request();
        Log.e(TAG,"request:" + request.toString());
        Log.e(TAG,"request header:" + request.headers());

        long t1 = System.nanoTime();
        okhttp3.Response response = chain.proceed(chain.request());
        long t2 = System.nanoTime();
        Log.e(TAG,String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();


        Log.e(TAG,"response body:" + content);

        Log.e(TAG,"==============================Interceptor End===============================");

        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}
