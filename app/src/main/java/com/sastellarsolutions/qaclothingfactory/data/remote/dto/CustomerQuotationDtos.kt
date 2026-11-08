package com.sastellarsolutions.qaclothingfactory.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

// ============================================================
// CUSTOMER QUOTATION LIST RESPONSE
// GET /api/CustomerQuotations
// ============================================================

data class CustomerQuotationsResponse(

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("count")
    val count: Int = 0,

    @SerializedName("totalCount")
    val totalCount: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("pageSize")
    val pageSize: Int = 20,

    @SerializedName("quotations")
    val quotations: List<CustomerQuotationDto> = emptyList()
)

// ============================================================
// CUSTOMER QUOTATION MODEL
// ============================================================

data class CustomerQuotationDto(

    @SerializedName("quotationID")
    val quotationID: Int = 0,

    @SerializedName("referenceNumber")
    val referenceNumber: String = "",

    @SerializedName("fullName")
    val fullName: String = "",

    @SerializedName("company")
    val company: String? = null,

    @SerializedName("email")
    val email: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("category")
    val category: String = "",

    @SerializedName("quantity")
    val quantity: Int = 0,

    @SerializedName("deadline")
    val deadline: String? = null,

    @SerializedName("specifications")
    val specifications: String = "",

    @SerializedName("status")
    val status: String = "Pending",

    @SerializedName("createdAtUtc")
    val createdAtUtc: String? = null,

    @SerializedName("reviewedByEmployeeID")
    val reviewedByEmployeeID: Int? = null,

    @SerializedName("reviewedAtUtc")
    val reviewedAtUtc: String? = null,

    @SerializedName("managerNotes")
    val managerNotes: String? = null,

    @SerializedName("quotedAmount")
    val quotedAmount: BigDecimal? = null
)

// ============================================================
// CUSTOMER QUOTATION DETAILS RESPONSE
// GET /api/CustomerQuotations/{id}
// ============================================================

data class CustomerQuotationDetailsResponse(

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("quotation")
    val quotation: CustomerQuotationDto? = null
)

// ============================================================
// REVIEW CUSTOMER QUOTATION REQUEST
// PUT /api/CustomerQuotations/{id}/review
// ============================================================

data class ReviewCustomerQuotationRequest(

    @SerializedName("status")
    val status: String,

    @SerializedName("managerNotes")
    val managerNotes: String? = null,

    @SerializedName("quotedAmount")
    val quotedAmount: BigDecimal? = null
)

// ============================================================
// REVIEW CUSTOMER QUOTATION RESPONSE
// ============================================================

data class ReviewCustomerQuotationResponse(

    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("quotationID")
    val quotationID: Int = 0,

    @SerializedName("referenceNumber")
    val referenceNumber: String = "",

    @SerializedName("status")
    val status: String = "",

    @SerializedName("quotedAmount")
    val quotedAmount: BigDecimal? = null,

    @SerializedName("reviewedByEmployeeID")
    val reviewedByEmployeeID: Int? = null,

    @SerializedName("reviewedAtUtc")
    val reviewedAtUtc: String? = null
)
