package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateMaterialRequestItem
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateMaterialRequestRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateMaterialRequestResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.MaterialRequestResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.MaterialCatalogItem
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProgressResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionReportsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.DeleteProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProductResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RawMaterialUsage
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateProductionBatchRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient

class ProductionRepository {

    private val productionApi = RetrofitClient.productionApi
    private val gson = Gson()

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    // ============================================================
    // PRODUCTION BATCHES
    // ============================================================

    suspend fun getProductionBatches(
        token: String
    ): Result<List<ProductionBatchResponse>> {
        return try {
            val response = productionApi.getProductionBatches(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.batches)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to connect to the production service. Please check your connection and try again."
            )
        }
    }

    suspend fun getProductionBatch(
        token: String,
        batchID: Int
    ): Result<ProductionBatchResponse> {
        return try {
            val response = productionApi.getProductionBatch(
                authorization = bearerToken(token),
                batchID = batchID
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.batch)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load the production batch.")
        }
    }

    // ============================================================
    // PRODUCTION PROGRESS
    // ============================================================

    suspend fun getProductionProgress(
        token: String
    ): Result<ProductionProgressResponse> {
        return try {
            val response = productionApi.getProductionProgress(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to load production progress. Please check your connection and try again."
            )
        }
    }

    // ============================================================
    // PRODUCTION REPORTS
    // ============================================================

    suspend fun getProductionReports(
        token: String
    ): Result<ProductionReportsResponse> {
        return try {
            val response = productionApi.getProductionReports(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to load production reports. Please check your connection and try again."
            )
        }
    }

    // ============================================================
    // PRODUCTS
    // ============================================================

    suspend fun getProducts(
        token: String
    ): Result<List<ProductionProductResponse>> {
        return try {
            val response = productionApi.getProducts(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.products)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load production products.")
        }
    }

    // ============================================================
    // SUPERVISORS
    // ============================================================

    suspend fun getSupervisors(
        token: String
    ): Result<List<ProductionSupervisorResponse>> {
        return try {
            val response = productionApi.getSupervisors(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.supervisors)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load supervisors.")
        }
    }

    // ============================================================
    // CREATE PRODUCTION BATCH
    // ============================================================

    suspend fun createProductionBatch(
        token: String,
        batchNumber: String,
        productID: Int,
        targetQuantity: Double,
        quantityProduced: Double,
        quantityDefective: Double?,
        rawMaterials: List<RawMaterialUsage>,
        productionDate: String,
        shift: String?,
        lineNumber: String?,
        supervisorID: Int?,
        productionStatus: String,
        startTime: String?,
        endTime: String?
    ): Result<CreateProductionBatchResponse> {
        return try {
            val rawMaterialJson =
                if (rawMaterials.isEmpty()) null else gson.toJson(rawMaterials)

            val request = CreateProductionBatchRequest(
                batchNumber = batchNumber.trim(),
                productID = productID,
                targetQuantity = targetQuantity,
                quantityProduced = quantityProduced,
                quantityDefective = quantityDefective,
                rawMaterialUsed = rawMaterialJson,
                productionDate = productionDate,
                shift = shift?.trim(),
                lineNumber = lineNumber?.trim()?.takeIf { it.isNotBlank() },
                supervisorID = supervisorID,
                productionStatus = productionStatus.trim(),
                startTime = startTime,
                endTime = endTime
            )

            val response = productionApi.createProductionBatch(
                authorization = bearerToken(token),
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to create the production batch. Please try again."
            )
        }
    }

    // ============================================================
    // UPDATE PRODUCTION BATCH
    // ============================================================

    suspend fun updateProductionBatch(
        token: String,
        batchID: Int,
        batchNumber: String,
        productID: Int,
        targetQuantity: Double,
        quantityProduced: Double,
        quantityDefective: Double?,
        rawMaterials: List<RawMaterialUsage>,
        productionDate: String,
        shift: String?,
        lineNumber: String?,
        supervisorID: Int?,
        productionStatus: String,
        startTime: String?,
        endTime: String?
    ): Result<UpdateProductionBatchResponse> {
        return try {
            val rawMaterialJson =
                if (rawMaterials.isEmpty()) null else gson.toJson(rawMaterials)

            val request = UpdateProductionBatchRequest(
                batchNumber = batchNumber.trim(),
                productID = productID,
                targetQuantity = targetQuantity,
                quantityProduced = quantityProduced,
                quantityDefective = quantityDefective,
                rawMaterialUsed = rawMaterialJson,
                productionDate = productionDate,
                shift = shift?.trim(),
                lineNumber = lineNumber?.trim()?.takeIf { it.isNotBlank() },
                supervisorID = supervisorID,
                productionStatus = productionStatus.trim(),
                startTime = startTime,
                endTime = endTime
            )

            val response = productionApi.updateProductionBatch(
                authorization = bearerToken(token),
                batchID = batchID,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to update the production batch. Please try again."
            )
        }
    }

    // ============================================================
    // DELETE PRODUCTION BATCH
    // ============================================================

    suspend fun deleteProductionBatch(
        token: String,
        batchID: Int
    ): Result<DeleteProductionBatchResponse> {
        return try {
            val response = productionApi.deleteProductionBatch(
                authorization = bearerToken(token),
                batchID = batchID
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(
                    message = getErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                message = "Unable to delete the production batch. Please try again."
            )
        }
    }

    // ============================================================
    // MATERIAL CATALOG - PRODUCTION MANAGER
    // ============================================================

    suspend fun getMaterialCatalog(
        token: String
    ): Result<List<MaterialCatalogItem>> {
        return try {
            val response = productionApi.getMaterialCatalog(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                val catalog = response.body()!!
                if (catalog.success) {
                    Result.Success(catalog.materials)
                } else {
                    Result.Error("Unable to load the raw material catalog.")
                }
            } else {
                Result.Error(
                    getMaterialRequestErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                "Unable to load the raw material catalog. Please check your connection and try again."
            )
        }
    }

    // ============================================================
    // MATERIAL REQUESTS - PRODUCTION MANAGER
    // ============================================================

    suspend fun createMaterialRequest(
        token: String,
        batchID: Int,
        items: List<CreateMaterialRequestItem>,
        notes: String? = null
    ): Result<CreateMaterialRequestResponse> {

        if (batchID <= 0) {
            return Result.Error("Please select a valid production batch.")
        }

        if (items.isEmpty()) {
            return Result.Error("Please select at least one raw material.")
        }

        if (items.any {
                it.materialID <= 0 ||
                        !it.quantityRequested.isFinite() ||
                        it.quantityRequested <= 0.0 ||
                        it.quantityRequested > 999999999.99 ||
                        it.quantityRequested * 100.0 !=
                        kotlin.math.round(it.quantityRequested * 100.0)
            }) {
            return Result.Error(
                "Each material must have a valid quantity with no more than two decimal places."
            )
        }

        if (items.map { it.materialID }.distinct().size != items.size) {
            return Result.Error(
                "The same raw material cannot be selected more than once."
            )
        }

        return try {
            val request = CreateMaterialRequestRequest(
                batchID = batchID,
                notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                items = items
            )

            val response = productionApi.createMaterialRequest(
                authorization = bearerToken(token),
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    getMaterialRequestErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                "Unable to submit the material request. Please check your connection and try again."
            )
        }
    }

    suspend fun getMaterialRequests(
        token: String
    ): Result<List<MaterialRequestResponse>> {
        return try {
            val response = productionApi.getMaterialRequests(
                authorization = bearerToken(token)
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.requests)
            } else {
                Result.Error(
                    getMaterialRequestErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                "Unable to load material requests. Please check your connection and try again."
            )
        }
    }

    suspend fun getMaterialRequest(
        token: String,
        requestID: Int
    ): Result<MaterialRequestResponse> {
        if (requestID <= 0) {
            return Result.Error("Invalid material request ID.")
        }

        return try {
            val response = productionApi.getMaterialRequest(
                authorization = bearerToken(token),
                requestID = requestID
            )

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.request)
            } else {
                Result.Error(
                    getMaterialRequestErrorMessage(
                        response.code(),
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (exception: Exception) {
            Result.Error(
                "Unable to load the material request. Please check your connection and try again."
            )
        }
    }

    // ============================================================
    // RAW MATERIAL PARSING
    // ============================================================

    fun parseRawMaterials(
        rawMaterialUsed: String?
    ): List<RawMaterialUsage> {
        if (rawMaterialUsed.isNullOrBlank()) return emptyList()

        return try {
            val type = object : TypeToken<List<RawMaterialUsage>>() {}.type
            gson.fromJson<List<RawMaterialUsage>>(
                rawMaterialUsed,
                type
            ) ?: emptyList()
        } catch (exception: Exception) {
            emptyList()
        }
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================

    private fun bearerToken(token: String): String {
        return if (token.startsWith("Bearer ", ignoreCase = true)) {
            token
        } else {
            "Bearer $token"
        }
    }

    // ============================================================
    // ERROR HANDLING
    // ============================================================

    private fun readErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            gson.fromJson(
                errorBody,
                ApiErrorResponse::class.java
            )?.message
        } catch (_: Exception) {
            null
        }
    }

    private fun getErrorMessage(
        statusCode: Int,
        errorBody: String?
    ): String {
        val backendMessage = readErrorMessage(errorBody)

        if (!backendMessage.isNullOrBlank()) {
            return backendMessage
        }

        return when (statusCode) {
            400 -> "Please check the production information and try again."
            401 -> "Your session has expired. Please sign in again."
            403 -> "You do not have permission to access this production feature."
            404 -> "The requested production record could not be found."
            409 -> "A production batch with this batch number already exists."
            500 -> "A server error occurred while processing the production request."
            else -> "The production request could not be completed."
        }
    }

    private fun getMaterialRequestErrorMessage(
        statusCode: Int,
        errorBody: String?
    ): String {
        val backendMessage = readErrorMessage(errorBody)

        if (!backendMessage.isNullOrBlank()) {
            return backendMessage
        }

        return when (statusCode) {
            400 -> "Please check the material request details and try again."
            401 -> "Your session has expired. Please sign in again."
            403 -> "You do not have permission to access this material request."
            404 -> "The material request or production batch could not be found."
            409 -> "The material request cannot be processed in its current state."
            500 -> "A server error occurred while processing the material request."
            else -> "The material request could not be completed."
        }
    }
}
