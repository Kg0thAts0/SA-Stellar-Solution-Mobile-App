package com.sastellarsolutions.qaclothingfactory.data.remote.dto

// ============================================================
// RAW MATERIAL
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

    val targetQuantity: Double,

    val quantityProduced: Double,
    val quantityDefective: Double?,

    val acceptedQuantity: Double,
    val progressPercentage: Double,

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

    val productionStatus: String,

    val qualityStatus: String?,

    val qualityCheckedBy: Int?,
    val qualityCheckedByName: String?,

    val qualityCheckDate: String?,
    val qualityNotes: String?,

    val recordedAt: String?
)


// ============================================================
// PRODUCTION BATCH RESPONSES
// ============================================================

data class ProductionBatchesResponse(
    val success: Boolean,
    val count: Int,
    val batches: List<ProductionBatchResponse>
)

data class ProductionBatchDetailsResponse(
    val success: Boolean,
    val batch: ProductionBatchResponse
)


// ============================================================
// PRODUCTS
// ============================================================

data class ProductionProductResponse(
    val productID: Int,
    val productCode: String,
    val productName: String,
    val category: String?,
    val unit: String,
    val currentStock: Double?
)

data class ProductionProductsResponse(
    val success: Boolean,
    val count: Int,
    val products: List<ProductionProductResponse>
)


// ============================================================
// SUPERVISORS
// ============================================================

data class ProductionSupervisorResponse(
    val employeeID: Int,
    val fullName: String,
    val emailAddress: String
)

data class ProductionSupervisorsResponse(
    val success: Boolean,
    val count: Int,
    val supervisors: List<ProductionSupervisorResponse>
)


// ============================================================
// CREATE PRODUCTION BATCH
// ============================================================

data class CreateProductionBatchRequest(
    val batchNumber: String,
    val productID: Int,

    val targetQuantity: Double,

    val quantityProduced: Double,
    val quantityDefective: Double?,

    val rawMaterialUsed: String?,

    val productionDate: String,

    val shift: String?,
    val lineNumber: String?,

    val supervisorID: Int?,

    val productionStatus: String,

    val startTime: String?,
    val endTime: String?
)

data class CreateProductionBatchResponse(
    val success: Boolean,
    val message: String,
    val batchID: Int,
    val batchNumber: String
)


// ============================================================
// UPDATE PRODUCTION BATCH
// ============================================================

data class UpdateProductionBatchRequest(
    val batchNumber: String,
    val productID: Int,

    val targetQuantity: Double,

    val quantityProduced: Double,
    val quantityDefective: Double?,

    val rawMaterialUsed: String?,

    val productionDate: String,

    val shift: String?,
    val lineNumber: String?,

    val supervisorID: Int?,

    val productionStatus: String,

    val startTime: String?,
    val endTime: String?
)

data class UpdateProductionBatchResponse(
    val success: Boolean,
    val message: String,
    val batchID: Int,
    val batchNumber: String
)


// ============================================================
// DELETE PRODUCTION BATCH
// ============================================================

data class DeleteProductionBatchResponse(
    val success: Boolean,
    val message: String,
    val batchID: Int,
    val batchNumber: String
)


// ============================================================
// PRODUCTION PROGRESS SUMMARY
// ============================================================

data class ProductionProgressSummary(
    val totalOrders: Int,
    val activeOrders: Int,
    val completedOrders: Int,
    val plannedOrders: Int,
    val cancelledOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalDefective: Double,
    val totalAccepted: Double,

    val overallProgressPercentage: Double
)


// ============================================================
// PRODUCTION PROGRESS BATCH
// ============================================================

data class ProductionProgressBatch(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val progressPercentage: Double,

    val productionStatus: String,

    val supervisorID: Int?,
    val supervisorName: String?,

    val lineNumber: String?,
    val shift: String?,

    val productionDate: String,

    val startTime: String?,
    val endTime: String?,

    val qualityStatus: String?
)


// ============================================================
// PRODUCTION PROGRESS RESPONSE
// ============================================================

data class ProductionProgressResponse(
    val success: Boolean,
    val summary: ProductionProgressSummary,
    val count: Int,
    val batches: List<ProductionProgressBatch>
)


// ============================================================
// PRODUCTION REPORT SUMMARY
// ============================================================

data class ProductionReportSummary(
    val totalOrders: Int,
    val plannedOrders: Int,
    val activeOrders: Int,
    val completedOrders: Int,
    val cancelledOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val targetAchievementPercentage: Double,
    val defectRatePercentage: Double
)


// ============================================================
// PRODUCTION REPORT - BY PRODUCT
// ============================================================

data class ProductionByProduct(
    val productID: Int,
    val productCode: String,
    val productName: String,

    val totalOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val achievementPercentage: Double
)


// ============================================================
// PRODUCTION REPORT - BY SHIFT
// ============================================================

data class ProductionByShift(
    val shift: String,
    val totalOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double
)


// ============================================================
// PRODUCTION REPORT - BY LINE
// ============================================================

data class ProductionByLine(
    val lineNumber: String,
    val totalOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double
)


// ============================================================
// PRODUCTION REPORT - SUPERVISOR PERFORMANCE
// ============================================================

data class SupervisorPerformance(
    val supervisorID: Int?,
    val supervisorName: String,

    val totalOrders: Int,
    val completedOrders: Int,

    val totalTarget: Double,
    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val achievementPercentage: Double,
    val defectRatePercentage: Double
)


// ============================================================
// PRODUCTION REPORT - BATCH HISTORY
// ============================================================

data class ProductionReportBatch(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val progressPercentage: Double,

    val productionStatus: String,
    val productionDate: String,

    val shift: String?,
    val lineNumber: String?,

    val supervisorID: Int?,
    val supervisorName: String?,

    val startTime: String?,
    val endTime: String?,

    val qualityStatus: String?
)


// ============================================================
// PRODUCTION REPORT RESPONSE
// ============================================================

data class ProductionReportsResponse(
    val success: Boolean,

    val summary: ProductionReportSummary,

    val productionByProduct: List<ProductionByProduct>,
    val productionByShift: List<ProductionByShift>,
    val productionByLine: List<ProductionByLine>,
    val supervisorPerformance: List<SupervisorPerformance>,

    val historyCount: Int,
    val batchHistory: List<ProductionReportBatch>
)
