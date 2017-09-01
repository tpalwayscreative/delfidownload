package com.learn2crack.filedownload;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface RetrofitInterface {

    @GET("AirDroid_Desktop_Client_3.5.4.0.exe")
    @Streaming
    Call<ResponseBody> downloadFile();
}
