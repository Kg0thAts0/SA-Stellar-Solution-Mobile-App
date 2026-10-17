package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
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

    suspend fun getProductionBatches(token: String): Result<List<ProductionBatchResponse>> {
        return try {
            val response = productionApi.getProductionBatches(authorization = bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.batches)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to connect to the production service. Please check your connection and try again.")
        }
    }

    suspend fun getProductionBatch(token: String, batchID: Int): Result<ProductionBatchResponse> {
        return try {
            val response = productionApi.getProductionBatch(
                authorization = bearerToken(token),
                batchID = batchID
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.batch)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load the production batch.")
        }
    }

    suspend fun getProductionProgress(token: String): Result<ProductionProgressResponse> {
        return try {
            val response = productionApi.getProductionProgress(authorization = bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load production progress. Please check your connection and try again.")
        }
    }

    suspend fun getProductionReports(token: String): Result<ProductionReportsResponse> {
        return try {
            val response = productionApi.getProductionReports(authorization = bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load production reports. Please check your connection and try again.")
        }
    }

    suspend fun getProducts(token: String): Result<List<ProductionProductResponse>> {
        return try {
            val response = productionApi.getProducts(authorization = bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.products)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load production products.")
        }
    }

    suspend fun getSupervisors(token: String): Result<List<ProductionSupervisorResponse>> {
        return try {
            val response = productionApi.getSupervisors(authorization = bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(data = response.body()!!.supervisors)
            } else {
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to load supervisors.")
        }
    }

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
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to create the production batch. Please try again.")
        }
    }

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
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to update the production batch. Please try again.")
        }
    }

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
                Result.Error(message = getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (exception: Exception) {
            Result.Error(message = "Unable to delete the production batch. Please try again.")
        }
    }

    fun parseRawMaterials(rawMaterialUsed: String?): List<RawMaterialUsage> {
        if (rawMaterialUsed.isNullOrBlank()) return emptyList()

        return try {
            val type = object : TypeToken<List<RawMaterialUsage>>() {}.type
            gson.fromJson<List<RawMaterialUsage>>(rawMaterialUsed, type) ?: emptyList()
        } catch (exception: Exception) {
            emptyList()
        }
    }

    private fun bearerToken(token: String): String {
        return if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
    }

    private fun readErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            gson.fromJson(errorBody, ApiErrorResponse::class.java)?.message
        } catch (_: Exception) {
            null
        }
    }

    private fun getErrorMessage(statusCode: Int, errorBody: String?): String {
        val backendMessage = readErrorMessage(errorBody)
        if (!backendMessage.isNullOrBlank()) return backendMessage

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
}
