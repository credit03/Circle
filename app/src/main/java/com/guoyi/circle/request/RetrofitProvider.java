package com.guoyi.circle.request;

import com.guoyi.circle.MyApplication;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: Season(ssseasonnn@gmail.com)
 * Date: 2016/12/9
 * Time: 16:46
 * FIXME
 */
public class RetrofitProvider {
    public static final String ServerIp = "http://192.168.43.178:8080";
    //public static final String ServerIp = "http://10.0.0.171:8080";

    /**
     * @return
     */
    public static Retrofit get() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(new GetOkHttpCookieInterceptor(MyApplication.getContext()));
        builder.addInterceptor(new AddOkHttpCookieIntercept(MyApplication.getContext()));
        builder.writeTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);

        return new Retrofit.Builder().baseUrl(ServerIp)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 上传文件使用
     *
     * @param time
     * @return
     */
    public static Retrofit getUpload(int time) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(new GetOkHttpCookieInterceptor(MyApplication.getContext()));
        builder.addInterceptor(new AddOkHttpCookieIntercept(MyApplication.getContext()));
        builder.writeTimeout(300, TimeUnit.SECONDS);
        builder.readTimeout(time, TimeUnit.SECONDS);
        builder.connectTimeout(time, TimeUnit.SECONDS);

        return new Retrofit.Builder().baseUrl(ServerIp)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
