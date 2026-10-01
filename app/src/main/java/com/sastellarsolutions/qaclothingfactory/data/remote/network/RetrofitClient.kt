package com.sastellarsolutions.qaclothingfactory.data.remote.network

import com.sastellarsolutions.qaclothingfactory.data.remote.api.AdminApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ========================================================
    // LOCAL DEVELOPMENT API
    // ========================================================
    //
    // Physical Android phone + ADB reverse:
    //
    // Phone:
    // http://127.0.0.1:5048
    //
    //              ↓
    //
    // USB / ADB reverse
    //
    //              ↓
    //
    // ASP.NET Core:
    // http://localhost:5048
    //
    // ========================================================

    private const val BASE_URL =
        "http://127.0.0.1:5048/"


    // ========================================================
    // HTTP LOGGING
    // ========================================================

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {

            level =
                HttpLoggingInterceptor.Level.BASIC
        }


    // ========================================================
    // OKHTTP
    // ========================================================

    private val httpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                loggingInterceptor
            )
            .connectTimeout(
                30,
                TimeUnit.SECONDS
            )
            .readTimeout(
                30,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )
            .build()


    // ========================================================
    // RETROFIT
    // ========================================================

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()


    // ========================================================
    // AUTHENTICATION API
    // ========================================================

    val authApi: AuthApiService =
        retrofit.create(
            AuthApiService::class.java
        )


    // ========================================================
    // ADMIN API
    // ========================================================

    val adminApi: AdminApiService =
        retrofit.create(
            AdminApiService::class.java
        )
}