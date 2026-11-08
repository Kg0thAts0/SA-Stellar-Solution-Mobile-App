package com.sastellarsolutions.qaclothingfactory.data.remote.dto

// ============================================================
// MATERIAL CATALOG ITEM
// GET /api/material-requests/catalog
// ============================================================

data class MaterialCatalogItem(
    val materialID: Int,
    val materialCode: String,
    val materialName: String,
    val unit: String,
    val currentStock: Double,
    val stockStatus: String
)

// ============================================================
// MATERIAL CATALOG RESPONSE
// ============================================================

data class MaterialCatalogResponse(
    val success: Boolean,
    val count: Int,
    val materials: List<MaterialCatalogItem>
)
