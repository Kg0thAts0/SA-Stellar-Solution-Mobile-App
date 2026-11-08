package com.sastellarsolutions.qaclothingfactory.data.remote.api

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CustomerQuotationDetailsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CustomerQuotationsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ReviewCustomerQuotationRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ReviewCustomerQuotationResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerQuotationApiService {

    // ============================================================
    // GET ALL CUSTOMER QUOTATIONS
    // Production Manager / Admin
    // ============================================================

    @GET("api/CustomerQuotations")
    suspend fun getCustomerQuotations(
        @Header("Authorization") authorization: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<CustomerQuotationsResponse>

    // ============================================================
    // GET CUSTOMER QUOTATION DETAILS
    // Production Manager / Admin
    // ============================================================

    @GET("api/CustomerQuotations/{id}")
    suspend fun getCustomerQuotation(
        @Header("Authorization") authorization: String,
        @Path("id") quotationID: Int
    ): Response<CustomerQuotationDetailsResponse>

    // ============================================================
    // REVIEW CUSTOMER QUOTATION
    // Production Manager / Admin
    // ============================================================

    @PUT("api/CustomerQuotations/{id}/review")
    suspend fun reviewCustomerQuotation(
        @Header("Authorization") authorization: String,
        @Path("id") quotationID: Int,
        @Body request: ReviewCustomerQuotationRequest
    ): Response<ReviewCustomerQuotationResponse>
}
