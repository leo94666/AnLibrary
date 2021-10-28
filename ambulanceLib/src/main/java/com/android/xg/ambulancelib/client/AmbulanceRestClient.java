package com.android.xg.ambulancelib.client;


import androidx.annotation.NonNull;


import com.android.xg.ambulancelib.api.AmbulanceRestService;
import com.top.arch.interceptor.LogInterceptor;
import com.top.arch.interceptor.SSLSocketClient;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class AmbulanceRestClient {
    private static AmbulanceRestService restService;

    public static AmbulanceRestService getApiUrl() {
        if (restService == null) {
            synchronized (AmbulanceRestClient.class) {
                if (restService == null) {
                    restService = new AmbulanceRestClient().getRetrofit();
                }
            }
        }
        return restService;
    }

    public AmbulanceRestService getRetrofit() {
        AmbulanceRestService restService = initRetrofit(initOkHttp()).create(AmbulanceRestService.class);
        return restService;
    }

    @NonNull
    private Retrofit initRetrofit(OkHttpClient client) {
        String url = "http://officeonline.e-lab.cn:8007";
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    private OkHttpClient initOkHttp() {
        return new OkHttpClient().newBuilder()
                .readTimeout(8, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(8, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(8, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor())
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())//配置
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();

    }
}