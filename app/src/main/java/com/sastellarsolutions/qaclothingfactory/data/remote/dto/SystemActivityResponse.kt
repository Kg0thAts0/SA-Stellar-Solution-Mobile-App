package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class SystemActivityResponse(

    val activityID: Int,

    val employeeID: Int?,

    val employeeName: String,

    val actionType: String,

    val description: String,

    val entityType: String?,

    val entityID: Int?,

    val activityTime: String,

    val ipAddress: String?
)