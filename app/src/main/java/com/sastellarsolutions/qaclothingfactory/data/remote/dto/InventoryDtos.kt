package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class InventoryDashboardResponse(
    val success: Boolean,
    val totalRawMaterials: Int,
    val lowStockMaterials: Int,
    val outOfStockMaterials: Int,
    val totalFinishedGoods: Int,
    val totalTransactions: Int,
    val rawMaterialStockValue: Double,
    val finishedGoodsStockValue: Double,
    val totalInventoryValue: Double
)

data class InventoryRawMaterial(
    val materialID: Int,
    val materialCode: String,
    val materialName: String,
    val category: String?,
    val unit: String,
    val currentStock: Double,
    val minimumStock: Double,
    val reorderLevel: Double,
    val unitCost: Double,
    val supplierInfo: String?,
    val lastUpdated: String?,
    val updatedBy: Int?,
    val stockStatus: String
)

data class InventoryRawMaterialsResponse(
    val success: Boolean,
    val count: Int,
    val materials: List<InventoryRawMaterial>
)

data class InventoryRawMaterialResponse(
    val success: Boolean,
    val material: InventoryRawMaterial
)

data class InventoryFinishedGood(
    val productID: Int,
    val productCode: String,
    val productName: String,
    val category: String?,
    val unit: String,
    val currentStock: Double,
    val unitPrice: Double,
    val productionBatchID: String?,
    val batchID: Int?,
    val qualityStatus: String?,
    val qualityInspectorID: Int?,
    val productionDate: String?,
    val recordedBy: Int?,
    val recordedAt: String?,
    val inspectionNotes: String?
)

data class InventoryFinishedGoodsResponse(
    val success: Boolean,
    val count: Int,
    val products: List<InventoryFinishedGood>
)

data class InventoryFinishedGoodResponse(
    val success: Boolean,
    val product: InventoryFinishedGood
)

data class InventoryStockResponse(
    val success: Boolean,
    val rawMaterialCount: Int,
    val finishedGoodsCount: Int,
    val rawMaterials: List<InventoryRawMaterial>,
    val finishedGoods: List<InventoryFinishedGood>
)

data class LowStockResponse(
    val success: Boolean,
    val count: Int,
    val materials: List<InventoryRawMaterial>
)

data class InventoryTransactionItem(
    val transactionID: Int,
    val materialID: Int?,
    val materialCode: String?,
    val materialName: String?,
    val productID: Int?,
    val productCode: String?,
    val productName: String?,
    val transactionType: String,
    val quantity: Double,
    val unitCost: Double?,
    val referenceNumber: String?,
    val notes: String?,
    val transactionDate: String?,
    val performedBy: Int?,
    val performedByName: String?,
    val itemType: String,
    val itemCode: String?,
    val itemName: String?
)

data class InventoryTransactionsResponse(
    val success: Boolean,
    val count: Int,
    val transactions: List<InventoryTransactionItem>
)

data class InventoryTransactionRequest(
    val materialID: Int? = null,
    val productID: Int? = null,
    val transactionType: String,
    val quantity: Double,
    val unitCost: Double? = null,
    val referenceNumber: String? = null,
    val notes: String? = null
)

data class CreatedInventoryTransaction(
    val transactionID: Int,
    val materialID: Int?,
    val productID: Int?,
    val transactionType: String,
    val quantity: Double,
    val unitCost: Double?,
    val referenceNumber: String?,
    val notes: String?,
    val transactionDate: String?,
    val performedBy: Int?,
    val performedByName: String?
)

data class CreateInventoryTransactionResponse(
    val success: Boolean,
    val message: String,
    val transaction: CreatedInventoryTransaction
)
