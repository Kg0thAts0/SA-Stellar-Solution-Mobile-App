package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ForgotPasswordRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ForgotPasswordResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RegisterRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RegisterResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ResetPasswordRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ResetPasswordResponse
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
    // REGISTER RESULT
    // ========================================================

    sealed class RegisterResult {

        data class Success(
            val response: RegisterResponse
        ) : RegisterResult()

        data class Error(
            val message: String
        ) : RegisterResult()
    }


    // ========================================================
    // FORGOT PASSWORD RESULT
    // ========================================================

    sealed class ForgotPasswordResult {

        data class Success(
            val response: ForgotPasswordResponse
        ) : ForgotPasswordResult()

        data class Error(
            val message: String
        ) : ForgotPasswordResult()
    }


    // ========================================================
    // RESET PASSWORD RESULT
    // ========================================================

    sealed class ResetPasswordResult {

        data class Success(
            val response: ResetPasswordResponse
        ) : ResetPasswordResult()

        data class Error(
            val message: String
        ) : ResetPasswordResult()
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
            // LOGIN SUCCESS
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
                // READ BACKEND ERROR
                // =============================================

                val errorMessage =
                    readErrorMessage(
                        errorBody =
                            response.errorBody()
                                ?.string()
                    )


                LoginResult.Error(
                    message =
                        errorMessage
                            ?: when (response.code()) {

                                400 ->
                                    "Invalid login information."

                                401 ->
                                    "Invalid email address or password."

                                403 ->
                                    "Your account is not active. Please contact an administrator."

                                404 ->
                                    "Login service was not found."

                                500 ->
                                    "A server error occurred. Please try again."

                                else ->
                                    "Login failed. Please try again."
                            }
                )
            }

        } catch (_: Exception) {

            LoginResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // REGISTER
    // ========================================================

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterResult {

        return try {

            // =================================================
            // CREATE REQUEST
            // =================================================

            val request =
                RegisterRequest(
                    firstName =
                        firstName.trim(),

                    lastName =
                        lastName.trim(),

                    email =
                        email.trim(),

                    password =
                        password,

                    confirmPassword =
                        confirmPassword
                )


            // =================================================
            // CALL API
            // =================================================

            val response =
                authApi.register(
                    request
                )


            // =================================================
            // REGISTRATION SUCCESS
            // =================================================

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                RegisterResult.Success(
                    response =
                        response.body()!!
                )

            } else {

                // =============================================
                // READ BACKEND ERROR MESSAGE
                // =============================================

                val errorMessage =
                    readErrorMessage(
                        errorBody =
                            response.errorBody()
                                ?.string()
                    )


                RegisterResult.Error(
                    message =
                        errorMessage
                            ?: when (response.code()) {

                                400 ->
                                    "Please check your registration information."

                                409 ->
                                    "An employee account with this email address already exists."

                                500 ->
                                    "A server error occurred while creating your account."

                                else ->
                                    "Registration failed. Please try again."
                            }
                )
            }

        } catch (_: Exception) {

            RegisterResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // FORGOT PASSWORD
    // ========================================================

    suspend fun forgotPassword(
        email: String
    ): ForgotPasswordResult {

        return try {

            // =================================================
            // CREATE REQUEST
            // =================================================

            val request =
                ForgotPasswordRequest(
                    email =
                        email.trim()
                )


            // =================================================
            // CALL API
            // =================================================

            val response =
                authApi.forgotPassword(
                    request
                )


            // =================================================
            // SUCCESS
            // =================================================

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                ForgotPasswordResult.Success(
                    response =
                        response.body()!!
                )

            } else {

                // =============================================
                // READ BACKEND ERROR
                // =============================================

                val errorMessage =
                    readErrorMessage(
                        errorBody =
                            response.errorBody()
                                ?.string()
                    )


                ForgotPasswordResult.Error(
                    message =
                        errorMessage
                            ?: when (response.code()) {

                                400 ->
                                    "Please enter a valid email address."

                                404 ->
                                    "Password reset service was not found."

                                500 ->
                                    "A server error occurred while requesting a password reset."

                                else ->
                                    "Unable to request a password reset. Please try again."
                            }
                )
            }

        } catch (_: Exception) {

            ForgotPasswordResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // RESET PASSWORD
    // ========================================================

    suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String,
        confirmPassword: String
    ): ResetPasswordResult {

        return try {

            // =================================================
            // CREATE REQUEST
            // =================================================

            val request =
                ResetPasswordRequest(
                    email =
                        email.trim(),

                    token =
                        token.trim(),

                    newPassword =
                        newPassword,

                    confirmPassword =
                        confirmPassword
                )


            // =================================================
            // CALL API
            // =================================================

            val response =
                authApi.resetPassword(
                    request
                )


            // =================================================
            // SUCCESS
            // =================================================

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                ResetPasswordResult.Success(
                    response =
                        response.body()!!
                )

            } else {

                // =============================================
                // READ BACKEND ERROR
                // =============================================

                val errorMessage =
                    readErrorMessage(
                        errorBody =
                            response.errorBody()
                                ?.string()
                    )


                ResetPasswordResult.Error(
                    message =
                        errorMessage
                            ?: when (response.code()) {

                                400 ->
                                    "The password reset request is invalid or has expired."

                                404 ->
                                    "Password reset service was not found."

                                500 ->
                                    "A server error occurred while resetting your password."

                                else ->
                                    "Unable to reset your password. Please try again."
                            }
                )
            }

        } catch (_: Exception) {

            ResetPasswordResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // READ API ERROR MESSAGE
    // ========================================================
    //
    // Login, registration, forgot password and reset password
    // all use the backend format:
    //
    // {
    //     "success": false,
    //     "message": "..."
    // }
    //
    // Keeping this logic in one place prevents duplicated
    // Gson parsing throughout the repository.
    //
    // ========================================================

    private fun readErrorMessage(
        errorBody: String?
    ): String? {

        if (errorBody.isNullOrBlank()) {
            return null
        }


        return try {

            Gson().fromJson(
                errorBody,
                ApiErrorResponse::class.java
            )?.message

        } catch (_: Exception) {

            null
        }
    }
}