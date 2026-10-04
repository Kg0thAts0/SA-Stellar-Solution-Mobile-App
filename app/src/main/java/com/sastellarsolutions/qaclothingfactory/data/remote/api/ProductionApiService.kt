package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchDetailsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchesResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProductsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionSupervisorsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductionApiService {

    // ========================================================
    // GET PRODUCTION BATCHES
    // ========================================================

    @GET("api/production/batches")
    suspend fun getProductionBatches(
        @Header("Authorization") authorization: String
    ): Response<ProductionBatchesResponse>


    // ========================================================
    // GET PRODUCTION BATCH
    // ========================================================

    @GET("api/production/batches/{id}")
    suspend fun getProductionBatch(
        @Header("Authorization") authorization: String,
        @Path("id") batchID: Int
    ): Response<ProductionBatchDetailsResponse>


    // ========================================================
    // GET PRODUCTS
    // ========================================================

    @GET("api/production/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String
    ): Response<ProductionProductsResponse>


    // ========================================================
    // GET SUPERVISORS
    // ========================================================

    @GET("api/production/supervisors")
    suspend fun getSupervisors(
        @Header("Authorization") authorization: String
    ): Response<ProductionSupervisorsResponse>


    // ========================================================
    // CREATE PRODUCTION BATCH
    // ========================================================

    @POST("api/production/batches")
    suspend fun createProductionBatch(
        @Header("Authorization") authorization: String,
        @Body request: CreateProductionBatchRequest
    ): Response<CreateProductionBatchResponse>
}