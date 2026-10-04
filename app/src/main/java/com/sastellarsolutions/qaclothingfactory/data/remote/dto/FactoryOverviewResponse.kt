package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class FactoryOverviewResponse(
    val employees: EmployeeOverview,
    val shiftTeams: ShiftOverview,
    val production: ProductionOverview,
    val inventory: InventoryOverview,
    val quality: QualityOverview
)

data class EmployeeOverview(
    val totalEmployees: Int,
    val activeEmployees: Int,
    val pendingEmployees: Int,
    val inactiveEmployees: Int
)

data class ShiftOverview(
    val todayShiftTeams: Int,
    val morningTeams: Int,
    val afternoonTeams: Int,
    val nightTeams: Int,
    val employeesScheduledToday: Int
)

data class ProductionOverview(
    val totalBatches: Int,
    val todayBatches: Int,
    val totalQuantityProduced: Double,
    val totalQuantityDefective: Double,
    val todayQuantityProduced: Double,
    val todayQuantityDefective: Double
)

data class InventoryOverview(
    val totalRawMaterials: Int,
    val lowStockMaterials: Int,
    val totalFinishedGoods: Int,
    val rawMaterialStockQuantity: Double,
    val finishedGoodsStockQuantity: Double,
    val totalInventoryTransactions: Int
)

data class QualityOverview(
    val pendingBatches: Int,
    val approvedBatches: Int,
    val rejectedBatches: Int,
    val pendingFinishedGoods: Int,
    val approvedFinishedGoods: Int,
    val rejectedFinishedGoods: Int
)