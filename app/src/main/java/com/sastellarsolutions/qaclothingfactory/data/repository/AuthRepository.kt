package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient

class AuthRepository {

    private val authApi =
        RetrofitClient.authApi

    // ========================================================
    // LOGIN RESULT
    // ========================================================

    sealed class LoginResult {

        data class Success(
            val response: LoginResponse
        ) : LoginResult()

        data class Error(
            val message: String
        ) : LoginResult()
    }


    // ========================================================
    // LOGIN
    // ========================================================

    suspend fun login(
        email: String,
        password: String
    ): LoginResult {

        return try {

            val response =
                authApi.login(
                    LoginRequest(
                        email = email.trim(),
                        password = password
                    )
                )


            // =================================================
            // SUCCESS
            // =================================================

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                LoginResult.Success(
                    response = response.body()!!
                )

            } else {

                // =============================================
                // READ BACKEND ERROR MESSAGE
                // =============================================

                val errorBody =
                    response.errorBody()
                        ?.string()

                val apiError =
                    try {

                        Gson().fromJson(
                            errorBody,
                            ApiErrorResponse::class.java
                        )

                    } catch (_: Exception) {

                        null
                    }


                LoginResult.Error(
                    message =
                        apiError?.message
                            ?: when (response.code()) {

                                401 ->
                                    "Invalid email address or password."

                                403 ->
                                    "Your account is not active."

                                else ->
                                    "Login failed. Please try again."
                            }
                )
            }

        } catch (exception: Exception) {

            LoginResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }
}