package com.sastellarsolutions.qaclothingfactory.data.repository

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient

class AdminRepository {

    private val adminApi =
        RetrofitClient.adminApi


    // ========================================================
    // DASHBOARD RESULT
    // ========================================================

    sealed class DashboardResult {

        data class Success(
            val dashboard: AdminDashboardResponse
        ) : DashboardResult()


        data class Error(
            val message: String
        ) : DashboardResult()
    }


    // ========================================================
    // GET ADMIN DASHBOARD
    // ========================================================

    suspend fun getDashboard(
        token: String
    ): DashboardResult {

        if (token.isBlank()) {

            return DashboardResult.Error(
                message = "Authentication token is missing."
            )
        }


        return try {

            val response =
                adminApi.getDashboard(
                    authorization =
                        "Bearer $token"
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                DashboardResult.Success(
                    dashboard =
                        response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to access the Admin dashboard."

                        404 ->
                            "Admin dashboard service was not found."

                        500 ->
                            "The server encountered an error."

                        else ->
                            "Unable to load the Admin dashboard."
                    }


                DashboardResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            DashboardResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }
}