package com.beta.fbinvite.invite;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Chu_xi on 2016/12/19.
 */

public class RetrofitManager {

    private OkHttpClient mHttpClient;
//    private Retrofit mRetrofit;

    public static RetrofitManager getInstance(@NonNull String baseUrl) {
        return new RetrofitManager(baseUrl);
    }

    public static RetrofitManager getInstance(@NonNull String baseUrl, OkHttpClient client) {
        return new RetrofitManager(baseUrl, client);
    }

    private RetrofitManager(String baseUrl) {
        this(baseUrl, null);
    }

    private RetrofitManager(String baseUrl, OkHttpClient client) {
        if (client == null) {
            client = buildOkHttpClient();
        }
        mHttpClient = client;
//        mRetrofit = buildRetrofit(baseUrl);
    }

    /**
     * create httpclient instance
     * with logger interceptor
     *
     * @return httpclient instance
     */
    private OkHttpClient buildOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(logging).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request origin = chain.request();
                Request request = origin.newBuilder()
                        .method(origin.method(), origin.body())
                        .build();
                return chain.proceed(request);
            }
        });
        return builder.build();
    }

    /**
     * create retrofit instance
     * according to httpclient、rx、gson ect
     *
     * @return retrofit instance
     */
//    private Retrofit buildRetrofit(@NonNull String baseUrl) {
//        Gson gson = new GsonBuilder().setLenient().create();
//        Retrofit.Builder builder = new Retrofit.Builder();
//        builder.client(mHttpClient)
//                .baseUrl(baseUrl)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson));
//        return builder.build();
//    }
//
//    public <T> T create(Class<T> clazz) {
//        return mRetrofit.create(clazz);
//    }
}
