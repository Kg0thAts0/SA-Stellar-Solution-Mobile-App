package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class SupervisorDashboardResponse(
    val success: Boolean,
    val totalAssignedBatches: Int,
    val activeBatches: Int,
    val completedBatches: Int,
    val totalShiftTeams: Int,
    val totalTargetQuantity: Double,
    val totalQuantityProduced: Double,
    val totalQuantityDefective: Double,
    val acceptedQuantity: Double,
    val overallProgressPercentage: Double
)

data class SupervisorBatch(
    val batchID: Int,
    val batchNumber: String,
    val productID: Int? = null,
    val productCode: String? = null,
    val productName: String? = null,
    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,
    val progressPercentage: Double,
    val productionStatus: String,
    val productionDate: String,
    val shift: String?,
    val lineNumber: String?,
    val startTime: String?,
    val endTime: String?,
    val rawMaterialUsed: String? = null,
    val qualityStatus: String?,
    val qualityCheckedBy: Int? = null,
    val qualityCheckDate: String? = null,
    val qualityNotes: String? = null,
    val recordedAt: String? = null
)

data class SupervisorAssignedWorkResponse(
    val success: Boolean,
    val count: Int,
    val batches: List<SupervisorBatch>
)

data class SupervisorAssignedWorkDetailResponse(
    val success: Boolean,
    val batch: SupervisorBatch
)

data class SupervisorTeamProgressSummary(
    val totalBatches: Int,
    val totalTargetQuantity: Double,
    val totalQuantityProduced: Double,
    val totalQuantityDefective: Double,
    val totalAcceptedQuantity: Double,
    val overallProgressPercentage: Double
)

data class SupervisorTeamProgressResponse(
    val success: Boolean,
    val summary: SupervisorTeamProgressSummary,
    val batches: List<SupervisorBatch>
)

data class SupervisorShiftTeam(
    val shiftTeamID: Int,
    val shiftDate: String,
    val shift: String,
    val supervisorID: Int,
    val lineNumber: String?,
    val totalEmployees: Int?,
    val notes: String?,
    val createdAt: String?
)

data class SupervisorShiftTeamsResponse(
    val success: Boolean,
    val count: Int,
    val teams: List<SupervisorShiftTeam>
)

data class SupervisorDailyUpdate(
    val batchID: Int,
    val batchNumber: String,
    val productCode: String?,
    val productName: String?,
    val productionDate: String,
    val shift: String?,
    val lineNumber: String?,
    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,
    val progressPercentage: Double,
    val productionStatus: String,
    val startTime: String?,
    val endTime: String?,
    val qualityStatus: String?
)

data class SupervisorDailyUpdatesResponse(
    val success: Boolean,
    val count: Int,
    val updates: List<SupervisorDailyUpdate>
)

data class SupervisorDailyUpdateRequest(
    val quantityProduced: Double,
    val quantityDefective: Double,
    val productionStatus: String,
    val startTime: String?,
    val endTime: String?
)

data class SupervisorDailyUpdateResult(
    val batchID: Int,
    val batchNumber: String,
    val targetQuantity: Double,
    val quantityProduced: Double,
    val quantityDefective: Double,
    val acceptedQuantity: Double,
    val progressPercentage: Double,
    val productionStatus: String,
    val productionDate: String,
    val shift: String?,
    val lineNumber: String?,
    val startTime: String?,
    val endTime: String?
)

data class SupervisorDailyUpdateResponse(
    val success: Boolean,
    val message: String,
    val batch: SupervisorDailyUpdateResult
)
