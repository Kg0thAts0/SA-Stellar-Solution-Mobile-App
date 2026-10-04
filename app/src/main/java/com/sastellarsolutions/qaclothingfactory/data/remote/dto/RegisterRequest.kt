package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class RegisterRequest(

    val firstName: String,

    val lastName: String,

    val email: String,

    val password: String,

    val confirmPassword: String
)