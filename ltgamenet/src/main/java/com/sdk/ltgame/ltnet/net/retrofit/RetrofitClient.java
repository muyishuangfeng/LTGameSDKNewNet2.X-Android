package com.sdk.ltgame.ltnet.net.retrofit;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.gentop.ltgame.ltgamesdkcore.common.LTGameOptions;
import com.gentop.ltgame.ltgamesdkcore.common.LTGameSdk;
import com.sdk.ltgame.ltnet.base.Constants;
import com.sdk.ltgame.ltnet.net.conver.GsonConverterFactory;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient implements BaseApi {
    private volatile static Retrofit retrofit = null;
    private Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
    private OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
    private Activity activity;

    public RetrofitClient(String baseUrl) {
        retrofitBuilder.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpBuilder.addInterceptor(getLoggerInterceptor())
                        .connectTimeout(30,TimeUnit.SECONDS)
                        .writeTimeout(30,TimeUnit.SECONDS)
                        .readTimeout(30,TimeUnit.SECONDS)
                        .build())
                .baseUrl(baseUrl);
    }

    /**
     * 构建retroft
     *
     * @return Retrofit对象
     */
    @Override
    public Retrofit getRetrofit(Activity activity) {
        this.activity=activity;
        if (retrofit == null) {
            //锁定代码块
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    retrofit = retrofitBuilder.build(); //创建retrofit对象
                }
            }
        }
        return retrofit;

    }


    @Override
    public OkHttpClient.Builder setInterceptor(Interceptor interceptor) {
        return httpBuilder.addInterceptor(interceptor);
    }

    @Override
    public Retrofit.Builder setConverterFactory(Converter.Factory factory) {
        return retrofitBuilder.addConverterFactory(factory);
    }

    /**
     * 日志拦截器
     * 将你访问的接口信息
     *
     * @return 拦截器
     */
    public HttpLoggingInterceptor getLoggerInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                String result = "";
                if (message.contains("HTTP FAILED")) {
                    result = message.substring(message.indexOf("HTTP FAILED"), message.length());
                }
                Intent intent = new Intent(Constants.MSG_SEND_EXCEPTION);
                intent.putExtra(Constants.MSG_EXCEPTION_NAME, result);
                activity.sendBroadcast(intent);
                LTGameOptions options = LTGameSdk.options();
                if (options.isDebug()) {
                    Log.e("ApiUrl", "--->" + message);
                }
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }
}
