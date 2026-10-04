package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class ShiftTeamResponse(
    val shiftTeamID: Int,
    val shiftDate: String,
    val shift: String,
    val supervisorID: Int,
    val supervisorName: String,
    val lineNumber: String?,
    val totalEmployees: Int?,
    val notes: String?,
    val createdAt: String
)