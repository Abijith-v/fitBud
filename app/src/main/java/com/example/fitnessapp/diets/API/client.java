package com.example.fitnessapp.diets.API;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class client {

    private static final String APIurl = "https://www.themealdb.com/api/json/v1/1/";

    public static Retrofit getclient() {
        return new Retrofit.Builder().baseUrl(APIurl).client(provideOkHttp())
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    private static Interceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private static okhttp3.OkHttpClient provideOkHttp() {
        return new OkHttpClient.Builder().connectTimeout(25, TimeUnit.SECONDS)
                .writeTimeout(25, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addNetworkInterceptor(provideLoggingInterceptor()).build();
    }

}
