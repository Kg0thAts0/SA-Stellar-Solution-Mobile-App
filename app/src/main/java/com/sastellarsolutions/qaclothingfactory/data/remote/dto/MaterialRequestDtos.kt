
package com.sastellarsolutions.qaclothingfactory.data.remote.dto

// ============================================================
// CREATE MATERIAL REQUEST
// ============================================================

data class CreateMaterialRequestItem(
    val materialID: Int,
    val quantityRequested: Double,
    val notes: String? = null
)

data class CreateMaterialRequestRequest(
    val batchID: Int,
    val notes: String? = null,
    val items: List<CreateMaterialRequestItem>
)

data class CreateMaterialRequestResponse(
    val success: Boolean,
    val message: String,
    val requestID: Int
)

// ============================================================
// MATERIAL REQUEST DETAILS
// ============================================================

data class MaterialRequestItemResponse(
    val requestItemID: Int,
    val materialID: Int,
    val materialCode: String,
    val materialName: String,
    val unit: String,
    val quantityRequested: Double,
    val quantityIssued: Double,
    val quantityRemaining: Double,
    val notes: String? = null,
    val currentStock: Double? = null
)

data class MaterialRequestResponse(
    val requestID: Int,
    val batchID: Int,
    val requestedBy: Int,
    val requestedAt: String,
    val status: String,
    val notes: String? = null,
    val items: List<MaterialRequestItemResponse>
)

data class MaterialRequestsResponse(
    val success: Boolean,
    val count: Int,
    val requests: List<MaterialRequestResponse>
)

data class MaterialRequestDetailsResponse(
    val success: Boolean,
    val request: MaterialRequestResponse
)

// ============================================================
// INVENTORY CLERK - ISSUE MATERIALS
// ============================================================

data class IssueMaterialRequestItem(
    val requestItemID: Int,
    val quantity: Double
)

data class IssueMaterialRequestRequest(
    val items: List<IssueMaterialRequestItem>
)

data class IssueMaterialRequestResponse(
    val success: Boolean,
    val message: String,
    val requestID: Int,
    val status: String,
    val issuedBy: Int
)
