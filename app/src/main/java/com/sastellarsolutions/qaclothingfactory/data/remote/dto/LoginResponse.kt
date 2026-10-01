package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class LoginResponse(

    val success: Boolean,

    val message: String,

    val token: String,

    val expiresAt: String,

    val employee: EmployeeResponse
)