package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminDashboardResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AdminApiService {

    @GET("api/admin/dashboard")
    suspend fun getDashboard(
        @Header("Authorization")
        authorization: String
    ): Response<AdminDashboardResponse>
}