package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class AdminEmployeeActionResponse(

    val success: Boolean,

    val message: String,

    val employee: AdminEmployeeResponse?
)