package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.DeleteProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchDetailsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchesResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProductsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProgressResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionReportsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionSupervisorsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateProductionBatchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface ProductionApiService {

    // ============================================================
    // PRODUCTION BATCHES
    // ============================================================

    @GET("api/production/batches")
    suspend fun getProductionBatches(
        @Header("Authorization") authorization: String
    ): Response<ProductionBatchesResponse>


    @GET("api/production/batches/{id}")
    suspend fun getProductionBatch(
        @Header("Authorization") authorization: String,
        @Path("id") batchID: Int
    ): Response<ProductionBatchDetailsResponse>


    // ============================================================
    // PRODUCTION PROGRESS
    // ============================================================

    @GET("api/production/progress")
    suspend fun getProductionProgress(
        @Header("Authorization") authorization: String
    ): Response<ProductionProgressResponse>


    // ============================================================
    // PRODUCTION REPORTS
    // ============================================================

    @GET("api/production/reports")
    suspend fun getProductionReports(
        @Header("Authorization") authorization: String
    ): Response<ProductionReportsResponse>


    // ============================================================
    // PRODUCTS
    // ============================================================

    @GET("api/production/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String
    ): Response<ProductionProductsResponse>


    // ============================================================
    // SUPERVISORS
    // ============================================================

    @GET("api/production/supervisors")
    suspend fun getSupervisors(
        @Header("Authorization") authorization: String
    ): Response<ProductionSupervisorsResponse>


    // ============================================================
    // CREATE PRODUCTION BATCH
    // ============================================================

    @POST("api/production/batches")
    suspend fun createProductionBatch(
        @Header("Authorization") authorization: String,
        @Body request: CreateProductionBatchRequest
    ): Response<CreateProductionBatchResponse>


    // ============================================================
    // UPDATE PRODUCTION BATCH
    // ============================================================

    @PUT("api/production/batches/{id}")
    suspend fun updateProductionBatch(
        @Header("Authorization") authorization: String,
        @Path("id") batchID: Int,
        @Body request: UpdateProductionBatchRequest
    ): Response<UpdateProductionBatchResponse>


    // ============================================================
    // DELETE PRODUCTION BATCH
    // ============================================================

    @DELETE("api/production/batches/{id}")
    suspend fun deleteProductionBatch(
        @Header("Authorization") authorization: String,
        @Path("id") batchID: Int
    ): Response<DeleteProductionBatchResponse>
}