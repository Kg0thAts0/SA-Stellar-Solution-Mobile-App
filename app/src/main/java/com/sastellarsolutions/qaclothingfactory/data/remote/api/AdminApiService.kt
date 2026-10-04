package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminEmployeeActionResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminEmployeeResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateShiftTeamRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.FactoryOverviewResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamActionResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SystemActivityResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateEmployeeRoleRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateShiftTeamRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApiService {

    // ========================================================
    // ADMIN DASHBOARD
    // ========================================================

    @GET("api/admin/dashboard")
    suspend fun getDashboard(
        @Header("Authorization")
        authorization: String
    ): Response<AdminDashboardResponse>


    // ========================================================
    // FACTORY OVERVIEW
    // ========================================================

    @GET("api/admin/factory-overview")
    suspend fun getFactoryOverview(
        @Header("Authorization")
        authorization: String
    ): Response<FactoryOverviewResponse>


    // ========================================================
    // SYSTEM ACTIVITY
    // ========================================================
    //
    // GET /api/admin/system-activity
    //
    // Returns the complete System Activity history.
    // Newest activities are returned first by the API.
    //
    // ========================================================

    @GET("api/admin/system-activity")
    suspend fun getSystemActivities(
        @Header("Authorization")
        authorization: String
    ): Response<List<SystemActivityResponse>>


    // ========================================================
    // GET SYSTEM ACTIVITY BY ID
    // ========================================================
    //
    // GET /api/admin/system-activity/{activityId}
    //
    // ========================================================

    @GET("api/admin/system-activity/{activityId}")
    suspend fun getSystemActivity(
        @Header("Authorization")
        authorization: String,

        @Path("activityId")
        activityId: Int
    ): Response<SystemActivityResponse>


    // ========================================================
    // GET ALL EMPLOYEES
    // ========================================================

    @GET("api/admin/employees")
    suspend fun getEmployees(
        @Header("Authorization")
        authorization: String
    ): Response<List<AdminEmployeeResponse>>


    // ========================================================
    // APPROVE EMPLOYEE
    // ========================================================

    @PUT("api/admin/employees/{employeeId}/approve")
    suspend fun approveEmployee(
        @Header("Authorization")
        authorization: String,

        @Path("employeeId")
        employeeId: Int
    ): Response<AdminEmployeeActionResponse>


    // ========================================================
    // DEACTIVATE EMPLOYEE
    // ========================================================

    @PUT("api/admin/employees/{employeeId}/deactivate")
    suspend fun deactivateEmployee(
        @Header("Authorization")
        authorization: String,

        @Path("employeeId")
        employeeId: Int
    ): Response<AdminEmployeeActionResponse>


    // ========================================================
    // REACTIVATE EMPLOYEE
    // ========================================================

    @PUT("api/admin/employees/{employeeId}/reactivate")
    suspend fun reactivateEmployee(
        @Header("Authorization")
        authorization: String,

        @Path("employeeId")
        employeeId: Int
    ): Response<AdminEmployeeActionResponse>


    // ========================================================
    // CHANGE EMPLOYEE ROLE
    // ========================================================

    @PUT("api/admin/employees/{employeeId}/role")
    suspend fun updateEmployeeRole(
        @Header("Authorization")
        authorization: String,

        @Path("employeeId")
        employeeId: Int,

        @Body
        request: UpdateEmployeeRoleRequest
    ): Response<AdminEmployeeActionResponse>


    // ========================================================
    // SHIFT TEAM MANAGEMENT
    // ========================================================


    // ========================================================
    // GET ALL SHIFT TEAMS
    // ========================================================

    @GET("api/admin/shift-teams")
    suspend fun getShiftTeams(
        @Header("Authorization")
        authorization: String
    ): Response<List<ShiftTeamResponse>>


    // ========================================================
    // GET SHIFT TEAM BY ID
    // ========================================================

    @GET("api/admin/shift-teams/{shiftTeamId}")
    suspend fun getShiftTeam(
        @Header("Authorization")
        authorization: String,

        @Path("shiftTeamId")
        shiftTeamId: Int
    ): Response<ShiftTeamResponse>


    // ========================================================
    // GET ACTIVE SUPERVISORS
    // ========================================================

    @GET("api/admin/shift-teams/supervisors")
    suspend fun getShiftTeamSupervisors(
        @Header("Authorization")
        authorization: String
    ): Response<List<ShiftTeamSupervisorResponse>>


    // ========================================================
    // CREATE SHIFT TEAM
    // ========================================================

    @POST("api/admin/shift-teams")
    suspend fun createShiftTeam(
        @Header("Authorization")
        authorization: String,

        @Body
        request: CreateShiftTeamRequest
    ): Response<ShiftTeamActionResponse>


    // ========================================================
    // UPDATE SHIFT TEAM
    // ========================================================

    @PUT("api/admin/shift-teams/{shiftTeamId}")
    suspend fun updateShiftTeam(
        @Header("Authorization")
        authorization: String,

        @Path("shiftTeamId")
        shiftTeamId: Int,

        @Body
        request: UpdateShiftTeamRequest
    ): Response<ShiftTeamActionResponse>


    // ========================================================
    // DELETE SHIFT TEAM
    // ========================================================

    @DELETE("api/admin/shift-teams/{shiftTeamId}")
    suspend fun deleteShiftTeam(
        @Header("Authorization")
        authorization: String,

        @Path("shiftTeamId")
        shiftTeamId: Int
    ): Response<ShiftTeamActionResponse>
}