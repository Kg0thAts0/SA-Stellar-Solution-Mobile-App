package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class ForgotPasswordResponse(

    val success: Boolean,

    val message: String,

    val resetToken: String? = null,

    val expiresInMinutes: Int? = null
)