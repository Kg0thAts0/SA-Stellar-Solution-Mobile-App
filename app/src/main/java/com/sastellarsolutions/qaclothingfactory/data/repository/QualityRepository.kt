package com.sastellarsolutions.qaclothingfactory.data.repository

import com.google.gson.Gson
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ApiErrorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDefectsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionDetails
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityReportsResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateQualityInspectionResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient


class QualityRepository {

    private val qualityApi = RetrofitClient.qualityApi
    private val gson = Gson()


    // ============================================================
    // RESULT
    // ============================================================

    sealed class Result<out T> {
        data class Success<T>(
            val data: T
        ) : Result<T>()

        data class Error(
            val message: String
        ) : Result<Nothing>()
    }


    // ============================================================
    // QUALITY DASHBOARD
    // ============================================================

    suspend fun getQualityDashboard(
        token: String
    ): Result<QualityDashboardResponse> {

        return try {

            val response =
                qualityApi.getQualityDashboard(
                    authorization = bearerToken(token)
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!
                )

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
                message =
                    "Unable to load the quality dashboard. " +
                            "Please check your connection and try again."
            )
        }
    }


    // ============================================================
    // ALL QUALITY INSPECTIONS
    // ============================================================

    suspend fun getQualityInspections(
        token: String,
        status: String? = null
    ): Result<List<QualityInspectionResponse>> {

        return try {

            val response =
                qualityApi.getQualityInspections(
                    authorization = bearerToken(token),
                    status = status
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!.inspections
                )

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
                message =
                    "Unable to load quality inspections. " +
                            "Please check your connection and try again."
            )
        }
    }


    // ============================================================
    // PENDING INSPECTIONS
    // ============================================================

    suspend fun getPendingInspections(
        token: String
    ): Result<List<QualityInspectionResponse>> {

        return try {

            val response =
                qualityApi.getPendingInspections(
                    authorization = bearerToken(token)
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!.inspections
                )

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
                message =
                    "Unable to load pending quality inspections. " +
                            "Please check your connection and try again."
            )
        }
    }


    // ============================================================
    // INSPECTION DETAILS
    // ============================================================

    suspend fun getQualityInspection(
        token: String,
        batchID: Int
    ): Result<QualityInspectionDetails> {

        return try {

            val response =
                qualityApi.getQualityInspection(
                    authorization = bearerToken(token),
                    batchID = batchID
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!.inspection
                )

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
                message =
                    "Unable to load the quality inspection."
            )
        }
    }


    // ============================================================
    // UPDATE QUALITY INSPECTION
    // APPROVE / REJECT
    // ============================================================

    suspend fun updateQualityInspection(
        token: String,
        batchID: Int,
        qualityStatus: String,
        qualityNotes: String?
    ): Result<UpdateQualityInspectionResponse> {

        return try {

            val cleanStatus =
                qualityStatus.trim()

            val cleanNotes =
                qualityNotes
                    ?.trim()
                    ?.takeIf {
                        it.isNotBlank()
                    }

            if (
                cleanStatus != "Approved" &&
                cleanStatus != "Rejected"
            ) {

                return Result.Error(
                    message =
                        "Quality status must be Approved or Rejected."
                )
            }

            if (
                cleanStatus == "Rejected" &&
                cleanNotes.isNullOrBlank()
            ) {

                return Result.Error(
                    message =
                        "Please provide quality notes when rejecting a batch."
                )
            }

            val request =
                QualityInspectionRequest(
                    qualityStatus = cleanStatus,
                    qualityNotes = cleanNotes
                )

            val response =
                qualityApi.updateQualityInspection(
                    authorization = bearerToken(token),
                    batchID = batchID,
                    request = request
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!
                )

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
                message =
                    "Unable to save the quality inspection. " +
                            "Please try again."
            )
        }
    }


    // ============================================================
    // APPROVE QUALITY INSPECTION
    // ============================================================

    suspend fun approveInspection(
        token: String,
        batchID: Int,
        qualityNotes: String?
    ): Result<UpdateQualityInspectionResponse> {

        return updateQualityInspection(
            token = token,
            batchID = batchID,
            qualityStatus = "Approved",
            qualityNotes = qualityNotes
        )
    }


    // ============================================================
    // REJECT QUALITY INSPECTION
    // ============================================================

    suspend fun rejectInspection(
        token: String,
        batchID: Int,
        qualityNotes: String
    ): Result<UpdateQualityInspectionResponse> {

        return updateQualityInspection(
            token = token,
            batchID = batchID,
            qualityStatus = "Rejected",
            qualityNotes = qualityNotes
        )
    }


    // ============================================================
    // DEFECTS
    // ============================================================

    suspend fun getQualityDefects(
        token: String
    ): Result<QualityDefectsResponse> {

        return try {

            val response =
                qualityApi.getQualityDefects(
                    authorization = bearerToken(token)
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!
                )

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
                message =
                    "Unable to load quality defects. " +
                            "Please check your connection and try again."
            )
        }
    }


    // ============================================================
    // QUALITY REPORTS
    // ============================================================

    suspend fun getQualityReports(
        token: String
    ): Result<QualityReportsResponse> {

        return try {

            val response =
                qualityApi.getQualityReports(
                    authorization = bearerToken(token)
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                Result.Success(
                    data = response.body()!!
                )

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
                message =
                    "Unable to load quality reports. " +
                            "Please check your connection and try again."
            )
        }
    }


    // ============================================================
    // BEARER TOKEN
    // ============================================================

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


    // ============================================================
    // READ BACKEND ERROR
    // ============================================================

    private fun readErrorMessage(
        errorBody: String?
    ): String? {

        if (
            errorBody.isNullOrBlank()
        ) {
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


    // ============================================================
    // ERROR MESSAGE
    // ============================================================

    private fun getErrorMessage(
        statusCode: Int,
        errorBody: String?
    ): String {

        val backendMessage =
            readErrorMessage(
                errorBody
            )

        if (
            !backendMessage.isNullOrBlank()
        ) {
            return backendMessage
        }

        return when (
            statusCode
        ) {

            400 ->
                "Please check the quality inspection information and try again."

            401 ->
                "Your session has expired. Please sign in again."

            403 ->
                "You do not have permission to access this quality-control feature."

            404 ->
                "The requested quality-control record could not be found."

            409 ->
                "The quality-control request conflicts with the current record."

            500 ->
                "A server error occurred while processing the quality-control request."

            else ->
                "The quality-control request could not be completed."
        }
    }
}