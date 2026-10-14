package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionDetailsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateQualityInspectionResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDefectsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityReportsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface QualityApiService {

    // ============================================================
    // QUALITY DASHBOARD
    // ============================================================

    @GET("api/quality/dashboard")
    suspend fun getQualityDashboard(
        @Header("Authorization") authorization: String
    ): Response<QualityDashboardResponse>


    // ============================================================
    // QUALITY INSPECTIONS
    // ============================================================

    @GET("api/quality/inspections")
    suspend fun getQualityInspections(
        @Header("Authorization") authorization: String,
        @Query("status") status: String? = null
    ): Response<QualityInspectionsResponse>


    // ============================================================
    // PENDING INSPECTIONS
    // ============================================================

    @GET("api/quality/inspections/pending")
    suspend fun getPendingInspections(
        @Header("Authorization") authorization: String
    ): Response<QualityInspectionsResponse>


    // ============================================================
    // INSPECTION DETAILS
    // ============================================================

    @GET("api/quality/inspections/{batchId}")
    suspend fun getQualityInspection(
        @Header("Authorization") authorization: String,
        @Path("batchId") batchID: Int
    ): Response<QualityInspectionDetailsResponse>


    // ============================================================
    // APPROVE / REJECT INSPECTION
    // ============================================================

    @PUT("api/quality/inspections/{batchId}")
    suspend fun updateQualityInspection(
        @Header("Authorization") authorization: String,
        @Path("batchId") batchID: Int,
        @Body request: QualityInspectionRequest
    ): Response<UpdateQualityInspectionResponse>


    // ============================================================
    // DEFECTS
    // ============================================================

    @GET("api/quality/defects")
    suspend fun getQualityDefects(
        @Header("Authorization") authorization: String
    ): Response<QualityDefectsResponse>


    // ============================================================
    // QUALITY REPORTS
    // ============================================================

    @GET("api/quality/reports")
    suspend fun getQualityReports(
        @Header("Authorization") authorization: String
    ): Response<QualityReportsResponse>
}