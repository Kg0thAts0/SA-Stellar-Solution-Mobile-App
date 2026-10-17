package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.*
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient

class InventoryRepository {

    private val inventoryApi = RetrofitClient.inventoryApi
    private val gson = Gson()

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    suspend fun getDashboard(token: String): Result<InventoryDashboardResponse> =
        execute("Unable to load the inventory dashboard.") {
            inventoryApi.getDashboard(bearerToken(token))
        }

    suspend fun getStock(token: String): Result<InventoryStockResponse> =
        execute("Unable to load inventory stock.") {
            inventoryApi.getStock(bearerToken(token))
        }

    suspend fun getRawMaterials(token: String): Result<List<InventoryRawMaterial>> {
        return try {
            val response = inventoryApi.getRawMaterials(bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.materials)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to load raw materials. Please check your connection and try again.")
        }
    }

    suspend fun getRawMaterial(token: String, materialID: Int): Result<InventoryRawMaterial> {
        return try {
            val response = inventoryApi.getRawMaterial(bearerToken(token), materialID)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.material)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to load the raw material.")
        }
    }

    suspend fun getFinishedGoods(token: String): Result<List<InventoryFinishedGood>> {
        return try {
            val response = inventoryApi.getFinishedGoods(bearerToken(token))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.products)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to load finished goods. Please check your connection and try again.")
        }
    }

    suspend fun getFinishedGood(token: String, productID: Int): Result<InventoryFinishedGood> {
        return try {
            val response = inventoryApi.getFinishedGood(bearerToken(token), productID)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.product)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to load the finished good.")
        }
    }

    suspend fun getLowStock(token: String): Result<LowStockResponse> =
        execute("Unable to load low-stock materials.") {
            inventoryApi.getLowStock(bearerToken(token))
        }

    suspend fun getTransactions(
        token: String,
        type: String? = null,
        materialID: Int? = null,
        productID: Int? = null
    ): Result<List<InventoryTransactionItem>> {
        return try {
            val response = inventoryApi.getTransactions(
                authorization = bearerToken(token),
                type = type,
                materialID = materialID,
                productID = productID
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.transactions)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to load stock movements. Please check your connection and try again.")
        }
    }

    suspend fun createTransaction(
        token: String,
        materialID: Int? = null,
        productID: Int? = null,
        transactionType: String,
        quantity: Double,
        unitCost: Double? = null,
        referenceNumber: String? = null,
        notes: String? = null
    ): Result<CreateInventoryTransactionResponse> {

        val cleanType = transactionType.trim()
        val validTypes = setOf("Receipt", "Issue", "Adjustment", "Return")

        if ((materialID == null) == (productID == null)) {
            return Result.Error("Select exactly one raw material or finished product.")
        }

        if (cleanType !in validTypes) {
            return Result.Error("Transaction type must be Receipt, Issue, Adjustment or Return.")
        }

        if (quantity <= 0.0) {
            return Result.Error("Quantity must be greater than zero.")
        }

        if (unitCost != null && unitCost < 0.0) {
            return Result.Error("Unit cost cannot be negative.")
        }

        val request = InventoryTransactionRequest(
            materialID = materialID,
            productID = productID,
            transactionType = cleanType,
            quantity = quantity,
            unitCost = unitCost,
            referenceNumber = referenceNumber?.trim()?.takeIf { it.isNotBlank() },
            notes = notes?.trim()?.takeIf { it.isNotBlank() }
        )

        return try {
            val response = inventoryApi.createTransaction(
                authorization = bearerToken(token),
                request = request
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("Unable to record the inventory transaction. Please check your connection and try again.")
        }
    }

    private suspend fun <T> execute(
        fallbackMessage: String,
        request: suspend () -> retrofit2.Response<T>
    ): Result<T> {
        return try {
            val response = request()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(getErrorMessage(response.code(), response.errorBody()?.string()))
            }
        } catch (_: Exception) {
            Result.Error("$fallbackMessage Please check your connection and try again.")
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
            400 -> "Please check the inventory information and try again."
            401 -> "Your session has expired. Please sign in again."
            403 -> "You do not have permission to access this inventory feature."
            404 -> "The requested inventory record could not be found."
            409 -> "The inventory request conflicts with the current stock record."
            500 -> "A server error occurred while processing the inventory request."
            else -> "The inventory request could not be completed."
        }
    }
}