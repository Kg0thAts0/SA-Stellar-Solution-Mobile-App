package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class CreateRawMaterialRequest(
    val materialCode: String,
    val materialName: String,
    val category: String?,
    val unit: String,
    val currentStock: Double,
    val minimumStock: Double,
    val reorderLevel: Double,
    val unitCost: Double,
    val supplierInfo: String?
)

data class CreateRawMaterialResponse(
    val success: Boolean,
    val message: String,
    val material: RawMaterialCreatedDto?
)

data class RawMaterialCreatedDto(
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
    val updatedBy: Int?
)
