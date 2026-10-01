package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}