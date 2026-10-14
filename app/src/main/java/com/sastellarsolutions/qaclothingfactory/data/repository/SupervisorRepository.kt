package com.sastellarsolutions.qaclothingfactory.data.repository

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorAssignedWorkDetailResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorAssignedWorkResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdateRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdateResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdatesResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorShiftTeamsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorTeamProgressResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient
import org.json.JSONObject
import retrofit2.Response

class SupervisorRepository {

    private val api = RetrofitClient.supervisorApi

    suspend fun getDashboard(token: String): Result<SupervisorDashboardResponse> =
        execute { api.getDashboard(bearer(token)) }

    suspend fun getAssignedWork(token: String): Result<SupervisorAssignedWorkResponse> =
        execute { api.getAssignedWork(bearer(token)) }

    suspend fun getAssignedWorkDetails(
        token: String,
        batchId: Int
    ): Result<SupervisorAssignedWorkDetailResponse> =
        execute { api.getAssignedWorkDetails(bearer(token), batchId) }

    suspend fun getTeamProgress(token: String): Result<SupervisorTeamProgressResponse> =
        execute { api.getTeamProgress(bearer(token)) }

    suspend fun getShiftTeams(token: String): Result<SupervisorShiftTeamsResponse> =
        execute { api.getShiftTeams(bearer(token)) }

    suspend fun getDailyUpdates(token: String): Result<SupervisorDailyUpdatesResponse> =
        execute { api.getDailyUpdates(bearer(token)) }

    suspend fun updateDailyProgress(
        token: String,
        batchId: Int,
        request: SupervisorDailyUpdateRequest
    ): Result<SupervisorDailyUpdateResponse> =
        execute {
            api.updateDailyProgress(
                authorization = bearer(token),
                batchId = batchId,
                request = request
            )
        }

    private fun bearer(token: String): String =
        if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"

    private suspend fun <T> execute(
        call: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = call()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(body)
            } else {
                Result.failure(
                    IllegalStateException(
                        extractErrorMessage(response)
                    )
                )
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun <T> extractErrorMessage(response: Response<T>): String {
        val fallback = when (response.code()) {
            400 -> "The request could not be processed."
            401 -> "Your session is not authorised. Please sign in again."
            403 -> "You do not have permission to perform this action."
            404 -> "The requested supervisor record was not found."
            else -> "Request failed with HTTP ${response.code()}."
        }

        val rawError = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }

        if (rawError.isNullOrBlank()) {
            return fallback
        }

        return try {
            val json = JSONObject(rawError)
            json.optString("message").takeIf { it.isNotBlank() } ?: fallback
        } catch (_: Exception) {
            fallback
        }
    }
}