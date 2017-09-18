package cn.com.shijizl.customerfiling.net;


import java.util.concurrent.TimeUnit;

import cn.com.shijizl.customerfiling.net.core.SecurityInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static final String BASE_URL_RELEASE = "http://59.110.233.135/";
    private static final String BASE_URL_DEBUG = "http://47.93.27.79/";
    private static final String BASE_URL = BASE_URL_RELEASE;

    private static RetrofitHelper mHelper;
    private static Retrofit mRetrofit;
    private static Retrofit mRetrofitUploads;

    public static void init() {
        getInstance();
    }

    public static RetrofitHelper getInstance() {
        if (mHelper == null) {
            synchronized (RetrofitHelper.class) {
                if (mHelper == null) {
                    mHelper = new RetrofitHelper();
                }
            }
        }
        return mHelper;
    }

    private RetrofitHelper() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SecurityInterceptor())
                .connectTimeout(10_000, TimeUnit.SECONDS)
                .readTimeout(10_000, TimeUnit.SECONDS)
                .writeTimeout(10_000, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //上传文件使用
        OkHttpClient client2 = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(50_000, TimeUnit.SECONDS)
                .readTimeout(50_000, TimeUnit.SECONDS)
                .build();

        mRetrofitUploads = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    static <T> T createApi(Class<T> clz) {
        return mRetrofit.create(clz);
    }

    static <T> T createUploadsApi(Class<T> clz) {
        return mRetrofitUploads.create(clz);
    }

}
