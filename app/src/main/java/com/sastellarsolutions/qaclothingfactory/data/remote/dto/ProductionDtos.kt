package com.sastellarsolutions.qaclothingfactory.data.remote.dto

// ============================================================
// RAW MATERIAL USAGE
// ============================================================

data class RawMaterialUsage(

    val material: String,

    val quantity: Double,

    val unit: String
)


// ============================================================
// PRODUCTION BATCH
// ============================================================

data class ProductionBatchResponse(

    val batchID: Int,

    val batchNumber: String,

    val productID: Int,

    val productCode: String,

    val productName: String,

    val quantityProduced: Double,

    val quantityDefective: Double?,

    val rawMaterialUsed: String?,

    val productionDate: String,

    val shift: String?,

    val lineNumber: String?,

    val startTime: String?,

    val endTime: String?,

    val supervisorID: Int?,

    val supervisorName: String?,

    val employeeID: Int?,

    val employeeName: String?,

    val qualityStatus: String?,

    val qualityCheckedBy: Int?,

    val qualityCheckedByName: String?,

    val qualityCheckDate: String?,

    val qualityNotes: String?,

    val recordedAt: String?
)


// ============================================================
// GET BATCHES RESPONSE
// ============================================================

data class ProductionBatchesResponse(

    val success: Boolean,

    val count: Int,

    val batches: List<ProductionBatchResponse>
)


// ============================================================
// GET SINGLE BATCH RESPONSE
// ============================================================

data class ProductionBatchDetailsResponse(

    val success: Boolean,

    val batch: ProductionBatchResponse
)


// ============================================================
// PRODUCT
// ============================================================

data class ProductionProductResponse(

    val productID: Int,

    val productCode: String,

    val productName: String,

    val category: String?,

    val unit: String,

    val currentStock: Double?
)


// ============================================================
// PRODUCTS RESPONSE
// ============================================================

data class ProductionProductsResponse(

    val success: Boolean,

    val count: Int,

    val products: List<ProductionProductResponse>
)


// ============================================================
// SUPERVISOR
// ============================================================

data class ProductionSupervisorResponse(

    val employeeID: Int,

    val fullName: String,

    val emailAddress: String
)


// ============================================================
// SUPERVISORS RESPONSE
// ============================================================

data class ProductionSupervisorsResponse(

    val success: Boolean,

    val count: Int,

    val supervisors: List<ProductionSupervisorResponse>
)


// ============================================================
// CREATE PRODUCTION BATCH REQUEST
// ============================================================

data class CreateProductionBatchRequest(

    val batchNumber: String,

    val productID: Int,

    val quantityProduced: Double,

    val quantityDefective: Double?,

    val rawMaterialUsed: String?,

    val productionDate: String,

    val shift: String?,

    val lineNumber: String?,

    val supervisorID: Int?,

    val startTime: String?,

    val endTime: String?
)


// ============================================================
// CREATE PRODUCTION BATCH RESPONSE
// ============================================================

data class CreateProductionBatchResponse(

    val success: Boolean,

    val message: String,

    val batchID: Int,

    val batchNumber: String
)