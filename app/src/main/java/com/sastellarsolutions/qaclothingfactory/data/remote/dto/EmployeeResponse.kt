package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class EmployeeResponse(

    val employeeID: Int,

    val fullName: String,

    val emailAddress: String,

    val role: String,

    val employeeStatus: String
)