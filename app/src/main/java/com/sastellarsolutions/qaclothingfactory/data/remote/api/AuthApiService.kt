package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ForgotPasswordRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ForgotPasswordResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RegisterRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RegisterResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ResetPasswordRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ResetPasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    // ========================================================
    // LOGIN
    // ========================================================

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    // ========================================================
    // REGISTER
    // ========================================================

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>


    // ========================================================
    // FORGOT PASSWORD
    // ========================================================

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>


    // ========================================================
    // RESET PASSWORD
    // ========================================================

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
}