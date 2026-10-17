package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class UpdateShiftTeamRequest(
    val shiftDate: String,
    val shift: String,
    val supervisorID: Int,
    val lineNumber: String?,
    val totalEmployees: Int?,
    val notes: String?
)