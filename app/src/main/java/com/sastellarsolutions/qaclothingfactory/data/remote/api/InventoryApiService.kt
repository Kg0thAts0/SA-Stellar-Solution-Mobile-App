package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface InventoryApiService {

    @GET("api/material-requests")
    suspend fun getMaterialRequests(
        @Header("Authorization") authorization: String
    ): Response<MaterialRequestsResponse>

    @GET("api/material-requests/{id}")
    suspend fun getMaterialRequest(
        @Header("Authorization") authorization: String,
        @Path("id") requestID: Int
    ): Response<MaterialRequestDetailsResponse>

    @POST("api/material-requests/{id}/issue")
    suspend fun issueMaterialRequest(
        @Header("Authorization") authorization: String,
        @Path("id") requestID: Int,
        @Body request: IssueMaterialRequestRequest
    ): Response<IssueMaterialRequestResponse>

    @GET("api/inventory/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authorization: String
    ): Response<InventoryDashboardResponse>

    @GET("api/inventory/stock")
    suspend fun getStock(
        @Header("Authorization") authorization: String
    ): Response<InventoryStockResponse>

    @GET("api/inventory/raw-materials")
    suspend fun getRawMaterials(
        @Header("Authorization") authorization: String
    ): Response<InventoryRawMaterialsResponse>

    @GET("api/inventory/raw-materials/{id}")
    suspend fun getRawMaterial(
        @Header("Authorization") authorization: String,
        @Path("id") materialID: Int
    ): Response<InventoryRawMaterialResponse>

    @POST("api/inventory/raw-materials")
    suspend fun createRawMaterial(
        @Header("Authorization") authorization: String,
        @Body request: CreateRawMaterialRequest
    ): Response<CreateRawMaterialResponse>

    @GET("api/inventory/finished-goods")
    suspend fun getFinishedGoods(
        @Header("Authorization") authorization: String
    ): Response<InventoryFinishedGoodsResponse>

    @GET("api/inventory/finished-goods/{id}")
    suspend fun getFinishedGood(
        @Header("Authorization") authorization: String,
        @Path("id") productID: Int
    ): Response<InventoryFinishedGoodResponse>

    @GET("api/inventory/low-stock")
    suspend fun getLowStock(
        @Header("Authorization") authorization: String
    ): Response<LowStockResponse>

    @GET("api/inventory/transactions")
    suspend fun getTransactions(
        @Header("Authorization") authorization: String,
        @Query("type") type: String? = null,
        @Query("materialId") materialID: Int? = null,
        @Query("productId") productID: Int? = null
    ): Response<InventoryTransactionsResponse>

    @POST("api/inventory/transactions")
    suspend fun createTransaction(
        @Header("Authorization") authorization: String,
        @Body request: InventoryTransactionRequest
    ): Response<CreateInventoryTransactionResponse>
}
