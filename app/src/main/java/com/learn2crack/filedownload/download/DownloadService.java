package com.learn2crack.filedownload.download;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Created by PC on 9/1/2017.
 */

public class DownloadService implements OnAttachmentDownloadListener{

    private Activity activity;
    public static final String TAG = DownloadService.class.getSimpleName();

    public DownloadService(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onAttachmentDownloadUpdate(int percent) {
        Log.d(TAG,"Downloading : "+ percent);
    }

    @Override
    public void onAttachmentDownloadedError() {

    }

    @Override
    public void onAttachmentDownloadedFinished() {

    }

    @Override
    public void onAttachmentDownloadedSuccess() {

    }

    public void downloadZipFileRx() {
        // https://github.com/yourusername/awesomegames/archive/master.zip
        RetrofitInterface downloadService = createService(RetrofitInterface.class, "https://github.com/");
        downloadService.downloadFileByUrlRx("delficode/delfiandroidcore/archive/master.zip")
                .flatMap(processResponse())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResult());

    }

    private Func1<Response<ResponseBody>, Observable<File>> processResponse() {
        return new Func1<Response<ResponseBody>, Observable<File>>() {
            @Override
            public Observable<File> call(Response<ResponseBody> responseBodyResponse) {
                return saveToDiskRx(responseBodyResponse);
            }
        };
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                try {
                    String header = response.headers().get("Content-Disposition");
                    String filename = header.replace("attachment; filename=", "");

                    //new File("/data/data/" + activity.getPackageName() + "/games").mkdir();
                    File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
                    //File destinationFile = new File("/data/data/" + activity.getPackageName() + "/games/" + filename);

                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(destinationFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();

                    subscriber.onNext(destinationFile);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observer<File> handleResult() {
        return new Observer<File>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.d(TAG, "Error " + e.getMessage());
            }
            @Override
            public void onNext(File file) {
                Log.d(TAG, "File downloaded to " + file.getAbsolutePath());
            }
        };
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpDownloadClientBuilder(this))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        return retrofit.create(serviceClass);
    }

    public OkHttpClient getOkHttpDownloadClientBuilder(final OnAttachmentDownloadListener progressListener) {
        OkHttpClient  httpClientBuilder = new OkHttpClient.Builder().addInterceptor( new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                if(progressListener == null) return chain.proceed(chain.request());

                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build();
        // You might want to increase the timeout
        httpClientBuilder.newBuilder().connectTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.newBuilder().writeTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.newBuilder().readTimeout(30, TimeUnit.MINUTES);
        return httpClientBuilder;
    }


}




