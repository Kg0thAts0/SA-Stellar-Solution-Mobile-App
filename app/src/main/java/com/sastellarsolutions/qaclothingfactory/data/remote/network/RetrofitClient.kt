package com.sastellarsolutions.qaclothingfactory.data.remote.network

import com.sastellarsolutions.qaclothingfactory.data.remote.api.AdminApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.AuthApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.InventoryApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.ProductionApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.QualityApiService
import com.sastellarsolutions.qaclothingfactory.data.remote.api.SupervisorApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // ========================================================
    // LOCAL DEVELOPMENT API
    // ========================================================

    private const val BASE_URL = "http://192.168.0.136:5048/"

    // ========================================================
    // HTTP LOGGING
    // ========================================================

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    // ========================================================
    // OKHTTP CLIENT
    // ========================================================

    private val httpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    // ========================================================
    // RETROFIT
    // ========================================================

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ========================================================
    // AUTHENTICATION API
    // ========================================================

    val authApi: AuthApiService =
        retrofit.create(AuthApiService::class.java)

    // ========================================================
    // ADMIN API
    // ========================================================

    val adminApi: AdminApiService =
        retrofit.create(AdminApiService::class.java)

    // ========================================================
    // PRODUCTION API
    // ========================================================

    val productionApi: ProductionApiService =
        retrofit.create(ProductionApiService::class.java)

    // ========================================================
    // QUALITY CONTROL API
    // ========================================================

    val qualityApi: QualityApiService =
        retrofit.create(QualityApiService::class.java)

    // ========================================================
    // INVENTORY CLERK API
    // ========================================================

    val inventoryApi: InventoryApiService =
        retrofit.create(InventoryApiService::class.java)

    // ========================================================
    // SUPERVISOR API
    // ========================================================

    val supervisorApi: SupervisorApiService =
        retrofit.create(SupervisorApiService::class.java)
}
