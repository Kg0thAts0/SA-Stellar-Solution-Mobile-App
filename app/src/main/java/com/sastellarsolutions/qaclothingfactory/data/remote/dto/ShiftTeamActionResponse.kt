package com.sastellarsolutions.qaclothingfactory.data.remote.dto

data class ShiftTeamActionResponse(
    val success: Boolean,
    val message: String,
    val shiftTeam: ShiftTeamResponse? = null
)