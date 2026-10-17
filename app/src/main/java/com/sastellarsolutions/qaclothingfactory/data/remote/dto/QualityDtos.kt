package com.sastellarsolutions.qaclothingfactory.data.remote.dto


// ============================================================
// QUALITY DASHBOARD
// ============================================================

data class QualityDashboardSummary(
    val totalBatches: Int,
    val pendingInspections: Int,
    val approvedBatches: Int,
    val rejectedBatches: Int,

    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val defectRatePercentage: Double,
    val approvalRatePercentage: Double
)


data class QualityDashboardResponse(
    val success: Boolean,
    val dashboard: QualityDashboardSummary
)


// ============================================================
// QUALITY INSPECTION
// ============================================================

data class QualityInspectionResponse(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val productionStatus: String,
    val productionDate: String,

    val shift: String?,
    val lineNumber: String?,

    val supervisorID: Int?,
    val supervisorName: String?,

    val qualityStatus: String,

    val qualityCheckedBy: Int?,
    val qualityCheckedByName: String?,

    val qualityCheckDate: String?,
    val qualityNotes: String?,

    val recordedAt: String?
)


// ============================================================
// QUALITY INSPECTIONS RESPONSE
// ============================================================

data class QualityInspectionsResponse(
    val success: Boolean,
    val count: Int,
    val inspections: List<QualityInspectionResponse>
)


// ============================================================
// QUALITY INSPECTION DETAILS
// ============================================================

data class QualityInspectionDetails(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val rawMaterialUsed: String?,

    val productionDate: String,

    val shift: String?,
    val lineNumber: String?,

    val supervisorID: Int?,
    val supervisorName: String?,

    val employeeID: Int?,
    val recordedByName: String?,

    val productionStatus: String,

    val qualityStatus: String,

    val qualityCheckedBy: Int?,
    val qualityCheckedByName: String?,

    val qualityCheckDate: String?,
    val qualityNotes: String?,

    val recordedAt: String?,

    val startTime: String?,
    val endTime: String?
)


data class QualityInspectionDetailsResponse(
    val success: Boolean,
    val inspection: QualityInspectionDetails
)


// ============================================================
// UPDATE QUALITY INSPECTION
// ============================================================

data class QualityInspectionRequest(
    val qualityStatus: String,
    val qualityNotes: String?
)


data class UpdatedQualityInspection(
    val batchID: Int,
    val batchNumber: String,

    val qualityStatus: String,

    val qualityCheckedBy: Int?,
    val qualityCheckDate: String?,
    val qualityNotes: String?
)


data class UpdateQualityInspectionResponse(
    val success: Boolean,
    val message: String,
    val inspection: UpdatedQualityInspection
)


// ============================================================
// QUALITY DEFECT
// ============================================================

data class QualityDefect(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val defectRatePercentage: Double,

    val qualityStatus: String,
    val qualityNotes: String?,

    val productionDate: String,

    val shift: String?,
    val lineNumber: String?
)


// ============================================================
// QUALITY DEFECTS RESPONSE
// ============================================================

data class QualityDefectsResponse(
    val success: Boolean,
    val count: Int,
    val totalDefective: Double,
    val defects: List<QualityDefect>
)


// ============================================================
// QUALITY REPORT SUMMARY
// ============================================================

data class QualityReportSummary(
    val totalBatches: Int,
    val pendingInspections: Int,
    val approvedBatches: Int,
    val rejectedBatches: Int,

    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val defectRatePercentage: Double,
    val approvalRatePercentage: Double
)


// ============================================================
// QUALITY REPORT - BY PRODUCT
// ============================================================

data class QualityByProduct(
    val productID: Int,
    val productCode: String,
    val productName: String,

    val totalBatches: Int,

    val approvedBatches: Int,
    val rejectedBatches: Int,
    val pendingBatches: Int,

    val totalProduced: Double,
    val totalAccepted: Double,
    val totalDefective: Double,

    val defectRatePercentage: Double
)


// ============================================================
// QUALITY REPORT - INSPECTION HISTORY
// ============================================================

data class QualityInspectionHistory(
    val batchID: Int,
    val batchNumber: String,

    val productID: Int,
    val productCode: String,
    val productName: String,

    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,

    val qualityStatus: String,

    val qualityCheckedBy: Int?,
    val qualityCheckedByName: String?,

    val qualityCheckDate: String?,
    val qualityNotes: String?,

    val productionDate: String,

    val shift: String?,
    val lineNumber: String?
)


// ============================================================
// QUALITY REPORT RESPONSE
// ============================================================

data class QualityReportsResponse(
    val success: Boolean,

    val summary: QualityReportSummary,

    val qualityByProduct: List<QualityByProduct>,

    val historyCount: Int,
    val inspectionHistory: List<QualityInspectionHistory>
)