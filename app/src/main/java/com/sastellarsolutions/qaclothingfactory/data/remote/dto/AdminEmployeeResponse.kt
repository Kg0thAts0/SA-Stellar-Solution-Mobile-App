package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class AdminEmployeeResponse(

    val employeeID: Int,

    val fullName: String,

    val emailAddress: String,

    // A newly registered employee has no role until
    // an administrator assigns one.
    val role: String?,

    val employeeStatus: String?,

    val createdAt: String?,

    val createdBy: Int?
)