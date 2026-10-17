package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorAssignedWorkDetailResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorAssignedWorkResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdateRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdateResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdatesResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorShiftTeamsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorTeamProgressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface SupervisorApiService {

    @GET("api/supervisor/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authorization: String
    ): Response<SupervisorDashboardResponse>

    @GET("api/supervisor/assigned-work")
    suspend fun getAssignedWork(
        @Header("Authorization") authorization: String
    ): Response<SupervisorAssignedWorkResponse>

    @GET("api/supervisor/assigned-work/{batchId}")
    suspend fun getAssignedWorkDetails(
        @Header("Authorization") authorization: String,
        @Path("batchId") batchId: Int
    ): Response<SupervisorAssignedWorkDetailResponse>

    @GET("api/supervisor/team-progress")
    suspend fun getTeamProgress(
        @Header("Authorization") authorization: String
    ): Response<SupervisorTeamProgressResponse>

    @GET("api/supervisor/shift-teams")
    suspend fun getShiftTeams(
        @Header("Authorization") authorization: String
    ): Response<SupervisorShiftTeamsResponse>

    @GET("api/supervisor/daily-updates")
    suspend fun getDailyUpdates(
        @Header("Authorization") authorization: String
    ): Response<SupervisorDailyUpdatesResponse>

    @PUT("api/supervisor/daily-updates/{batchId}")
    suspend fun updateDailyProgress(
        @Header("Authorization") authorization: String,
        @Path("batchId") batchId: Int,
        @Body request: SupervisorDailyUpdateRequest
    ): Response<SupervisorDailyUpdateResponse>
}
