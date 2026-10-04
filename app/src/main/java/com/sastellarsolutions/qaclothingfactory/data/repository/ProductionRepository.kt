package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProductResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RawMaterialUsage
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient


class ProductionRepository {

    private val productionApi =
        RetrofitClient.productionApi

    private val gson =
        Gson()


    // ========================================================
    // GENERAL RESULT
    // ========================================================

    sealed class Result<out T> {

        data class Success<T>(
            val data: T
        ) : Result<T>()

        data class Error(
            val message: String
        ) : Result<Nothing>()
    }


    // ========================================================
    // GET ALL PRODUCTION BATCHES
    // ========================================================

    suspend fun getProductionBatches(
        token: String
    ): Result<List<ProductionBatchResponse>> {

        return try {

            val response =
                productionApi.getProductionBatches(
                    authorization =
                        bearerToken(token)
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data =
                        response.body()!!.batches
                )

            } else {

                Result.Error(
                    message =
                        getErrorMessage(
                            response.code(),
                            response.errorBody()?.string()
                        )
                )
            }

        } catch (exception: Exception) {

            Result.Error(
                message =
                    "Unable to connect to the production service. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET PRODUCTION BATCH
    // ========================================================

    suspend fun getProductionBatch(
        token: String,
        batchID: Int
    ): Result<ProductionBatchResponse> {

        return try {

            val response =
                productionApi.getProductionBatch(
                    authorization =
                        bearerToken(token),

                    batchID =
                        batchID
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data =
                        response.body()!!.batch
                )

            } else {

                Result.Error(
                    message =
                        getErrorMessage(
                            response.code(),
                            response.errorBody()?.string()
                        )
                )
            }

        } catch (exception: Exception) {

            Result.Error(
                message =
                    "Unable to load the production batch."
            )
        }
    }


    // ========================================================
    // GET PRODUCTS
    // ========================================================

    suspend fun getProducts(
        token: String
    ): Result<List<ProductionProductResponse>> {

        return try {

            val response =
                productionApi.getProducts(
                    authorization =
                        bearerToken(token)
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data =
                        response.body()!!.products
                )

            } else {

                Result.Error(
                    message =
                        getErrorMessage(
                            response.code(),
                            response.errorBody()?.string()
                        )
                )
            }

        } catch (exception: Exception) {

            Result.Error(
                message =
                    "Unable to load production products."
            )
        }
    }


    // ========================================================
    // GET SUPERVISORS
    // ========================================================

    suspend fun getSupervisors(
        token: String
    ): Result<List<ProductionSupervisorResponse>> {

        return try {

            val response =
                productionApi.getSupervisors(
                    authorization =
                        bearerToken(token)
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data =
                        response.body()!!.supervisors
                )

            } else {

                Result.Error(
                    message =
                        getErrorMessage(
                            response.code(),
                            response.errorBody()?.string()
                        )
                )
            }

        } catch (exception: Exception) {

            Result.Error(
                message =
                    "Unable to load supervisors."
            )
        }
    }


    // ========================================================
    // CREATE PRODUCTION BATCH
    // ========================================================

    suspend fun createProductionBatch(
        token: String,
        batchNumber: String,
        productID: Int,
        quantityProduced: Double,
        quantityDefective: Double?,
        rawMaterials: List<RawMaterialUsage>,
        productionDate: String,
        shift: String?,
        lineNumber: String?,
        supervisorID: Int?,
        startTime: String?,
        endTime: String?
    ): Result<CreateProductionBatchResponse> {

        return try {

            // Convert our strongly typed Kotlin raw-material list
            // into the JSON string expected by the current backend.

            val rawMaterialJson =
                if (rawMaterials.isEmpty()) {

                    null

                } else {

                    gson.toJson(
                        rawMaterials
                    )
                }


            val request =
                CreateProductionBatchRequest(
                    batchNumber =
                        batchNumber.trim(),

                    productID =
                        productID,

                    quantityProduced =
                        quantityProduced,

                    quantityDefective =
                        quantityDefective,

                    rawMaterialUsed =
                        rawMaterialJson,

                    productionDate =
                        productionDate,

                    shift =
                        shift?.trim(),

                    lineNumber =
                        lineNumber
                            ?.trim()
                            ?.takeIf {
                                it.isNotBlank()
                            },

                    supervisorID =
                        supervisorID,

                    startTime =
                        startTime,

                    endTime =
                        endTime
                )


            val response =
                productionApi.createProductionBatch(
                    authorization =
                        bearerToken(token),

                    request =
                        request
                )


            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data =
                        response.body()!!
                )

            } else {

                Result.Error(
                    message =
                        getErrorMessage(
                            response.code(),
                            response.errorBody()?.string()
                        )
                )
            }

        } catch (exception: Exception) {

            Result.Error(
                message =
                    "Unable to create the production batch. Please try again."
            )
        }
    }


    // ========================================================
    // PARSE RAW MATERIALS
    // ========================================================

    fun parseRawMaterials(
        rawMaterialUsed: String?
    ): List<RawMaterialUsage> {

        if (rawMaterialUsed.isNullOrBlank()) {
            return emptyList()
        }


        return try {

            val type =
                object :
                    TypeToken<List<RawMaterialUsage>>() {}
                    .type


            gson.fromJson<List<RawMaterialUsage>>(
                rawMaterialUsed,
                type
            ) ?: emptyList()

        } catch (exception: Exception) {

            emptyList()
        }
    }


    // ========================================================
    // CREATE BEARER TOKEN
    // ========================================================

    private fun bearerToken(
        token: String
    ): String {

        return if (
            token.startsWith(
                "Bearer ",
                ignoreCase = true
            )
        ) {

            token

        } else {

            "Bearer $token"
        }
    }


    // ========================================================
    // READ API ERROR MESSAGE
    // ========================================================

    private fun readErrorMessage(
        errorBody: String?
    ): String? {

        if (errorBody.isNullOrBlank()) {
            return null
        }


        return try {

            gson.fromJson(
                errorBody,
                ApiErrorResponse::class.java
            )?.message

        } catch (_: Exception) {

            null
        }
    }


    // ========================================================
    // GET ERROR MESSAGE
    // ========================================================

    private fun getErrorMessage(
        statusCode: Int,
        errorBody: String?
    ): String {

        val backendMessage =
            readErrorMessage(
                errorBody
            )


        if (!backendMessage.isNullOrBlank()) {
            return backendMessage
        }


        return when (statusCode) {

            400 ->
                "Please check the production information and try again."

            401 ->
                "Your session has expired. Please sign in again."

            403 ->
                "You do not have permission to access this production feature."

            404 ->
                "The requested production record could not be found."

            409 ->
                "A production batch with this batch number already exists."

            500 ->
                "A server error occurred while processing the production request."

            else ->
                "The production request could not be completed."
        }
    }
}