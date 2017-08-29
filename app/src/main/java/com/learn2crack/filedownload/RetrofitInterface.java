package com.learn2crack.filedownload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface RetrofitInterface {

    @GET("itc_android.rar")
    @Streaming
    Call<ResponseBody> downloadFile();
}
