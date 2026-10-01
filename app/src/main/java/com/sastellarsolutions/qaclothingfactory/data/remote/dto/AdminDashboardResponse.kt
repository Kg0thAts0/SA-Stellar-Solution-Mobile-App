package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class AdminDashboardResponse(

    val success: Boolean,

    // Employees
    val totalEmployees: Int,
    val activeEmployees: Int,
    val pendingEmployees: Int,
    val inactiveEmployees: Int,

    // Production
    val totalProductionBatches: Int,
    val pendingQualityChecks: Int,
    val approvedBatches: Int,
    val rejectedBatches: Int,

    // Inventory
    val totalRawMaterials: Int,
    val lowStockMaterials: Int,

    // Finished Goods
    val totalFinishedGoods: Int,

    // Operations
    val totalInventoryTransactions: Int,
    val totalShiftTeams: Int
)