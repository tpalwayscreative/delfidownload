package com.learn2crack.filedownload.download;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by PC on 9/1/2017.
 */

public class DownloadserviceAction {

    public Retrofit getDownloadRetrofit(OnAttachmentDownloadListener listener) {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.43.135/retro/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpDownloadClientBuilder(listener).build())
                .build();
    }

    public OkHttpClient.Builder getOkHttpDownloadClientBuilder(final OnAttachmentDownloadListener progressListener) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // You might want to increase the timeout
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(0, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES);

        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if(progressListener == null) return chain.proceed(chain.request());

                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });
        return httpClientBuilder;
    }

}
