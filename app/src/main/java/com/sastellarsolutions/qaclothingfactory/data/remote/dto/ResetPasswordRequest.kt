package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class ResetPasswordRequest(

    val email: String,

    val token: String,

    val newPassword: String,

    val confirmPassword: String
)