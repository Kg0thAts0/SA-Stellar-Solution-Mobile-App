package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class RegisterResponse(

    val success: Boolean,

    val message: String,

    val employeeID: Int,

    val fullName: String,

    val emailAddress: String,

    val role: String?,

    val employeeStatus: String
)