package com.sastellarsolutions.qaclothingfactory.data.repository

import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminEmployeeResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateShiftTeamRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.FactoryOverviewResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SystemActivityResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateEmployeeRoleRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.UpdateShiftTeamRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient


class AdminRepository {

    private val adminApi =
        RetrofitClient.adminApi


    // ========================================================
    // DASHBOARD RESULT
    // ========================================================

    sealed class DashboardResult {

        data class Success(
            val dashboard: AdminDashboardResponse
        ) : DashboardResult()

        data class Error(
            val message: String
        ) : DashboardResult()
    }


    // ========================================================
    // FACTORY OVERVIEW RESULT
    // ========================================================

    sealed class FactoryOverviewResult {

        data class Success(
            val overview: FactoryOverviewResponse
        ) : FactoryOverviewResult()

        data class Error(
            val message: String
        ) : FactoryOverviewResult()
    }


    // ========================================================
    // SYSTEM ACTIVITY RESULT
    // ========================================================

    sealed class SystemActivityResult {

        data class Success(
            val activities: List<SystemActivityResponse>
        ) : SystemActivityResult()

        data class Error(
            val message: String
        ) : SystemActivityResult()
    }


    // ========================================================
    // EMPLOYEES RESULT
    // ========================================================

    sealed class EmployeesResult {

        data class Success(
            val employees: List<AdminEmployeeResponse>
        ) : EmployeesResult()

        data class Error(
            val message: String
        ) : EmployeesResult()
    }


    // ========================================================
    // EMPLOYEE ACTION RESULT
    // ========================================================

    sealed class EmployeeActionResult {

        data class Success(
            val message: String,
            val employee: AdminEmployeeResponse?
        ) : EmployeeActionResult()

        data class Error(
            val message: String
        ) : EmployeeActionResult()
    }


    // ========================================================
    // SHIFT TEAMS RESULT
    // ========================================================

    sealed class ShiftTeamsResult {

        data class Success(
            val shiftTeams: List<ShiftTeamResponse>
        ) : ShiftTeamsResult()

        data class Error(
            val message: String
        ) : ShiftTeamsResult()
    }


    // ========================================================
    // SHIFT TEAM RESULT
    // ========================================================

    sealed class ShiftTeamResult {

        data class Success(
            val shiftTeam: ShiftTeamResponse
        ) : ShiftTeamResult()

        data class Error(
            val message: String
        ) : ShiftTeamResult()
    }


    // ========================================================
    // SHIFT TEAM SUPERVISORS RESULT
    // ========================================================

    sealed class ShiftTeamSupervisorsResult {

        data class Success(
            val supervisors: List<ShiftTeamSupervisorResponse>
        ) : ShiftTeamSupervisorsResult()

        data class Error(
            val message: String
        ) : ShiftTeamSupervisorsResult()
    }


    // ========================================================
    // SHIFT TEAM ACTION RESULT
    // ========================================================

    sealed class ShiftTeamActionResult {

        data class Success(
            val message: String,
            val shiftTeam: ShiftTeamResponse?
        ) : ShiftTeamActionResult()

        data class Error(
            val message: String
        ) : ShiftTeamActionResult()
    }


    // ========================================================
    // GET ADMIN DASHBOARD
    // ========================================================

    suspend fun getDashboard(
        token: String
    ): DashboardResult {

        if (token.isBlank()) {
            return DashboardResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getDashboard(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                DashboardResult.Success(
                    dashboard = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to access the Admin dashboard."

                        404 ->
                            "Admin dashboard service was not found."

                        500 ->
                            "The server encountered an error."

                        else ->
                            "Unable to load the Admin dashboard."
                    }

                DashboardResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            DashboardResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET FACTORY OVERVIEW
    // ========================================================

    suspend fun getFactoryOverview(
        token: String
    ): FactoryOverviewResult {

        if (token.isBlank()) {
            return FactoryOverviewResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getFactoryOverview(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                FactoryOverviewResult.Success(
                    overview = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to view the Factory Overview."

                        404 ->
                            "Factory Overview service was not found."

                        500 ->
                            "The server encountered an error while loading the Factory Overview."

                        else ->
                            "Unable to load the Factory Overview."
                    }

                FactoryOverviewResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            FactoryOverviewResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET SYSTEM ACTIVITY
    // ========================================================

    suspend fun getSystemActivities(
        token: String
    ): SystemActivityResult {

        if (token.isBlank()) {
            return SystemActivityResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getSystemActivities(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                SystemActivityResult.Success(
                    activities = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to view System Activity."

                        404 ->
                            "System Activity service was not found."

                        500 ->
                            "The server encountered an error while loading System Activity."

                        else ->
                            "Unable to load System Activity."
                    }

                SystemActivityResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            SystemActivityResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET ALL EMPLOYEES
    // ========================================================

    suspend fun getEmployees(
        token: String
    ): EmployeesResult {

        if (token.isBlank()) {
            return EmployeesResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getEmployees(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                EmployeesResult.Success(
                    employees = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to manage employees."

                        404 ->
                            "Employee management service was not found."

                        500 ->
                            "The server encountered an error while loading employees."

                        else ->
                            "Unable to load employees."
                    }

                EmployeesResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            EmployeesResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // APPROVE EMPLOYEE
    // ========================================================

    suspend fun approveEmployee(
        token: String,
        employeeId: Int
    ): EmployeeActionResult {

        if (token.isBlank()) {
            return EmployeeActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (employeeId <= 0) {
            return EmployeeActionResult.Error(
                message = "Invalid employee ID."
            )
        }

        return try {

            val response =
                adminApi.approveEmployee(
                    authorization = "Bearer $token",
                    employeeId = employeeId
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                EmployeeActionResult.Success(
                    message = body.message,
                    employee = body.employee
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "This employee cannot be approved. Only pending employees can be approved."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to approve employees."

                        404 ->
                            "The employee could not be found."

                        500 ->
                            "The server encountered an error while approving the employee."

                        else ->
                            "Unable to approve the employee."
                    }

                EmployeeActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            EmployeeActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // DEACTIVATE EMPLOYEE
    // ========================================================

    suspend fun deactivateEmployee(
        token: String,
        employeeId: Int
    ): EmployeeActionResult {

        if (token.isBlank()) {
            return EmployeeActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (employeeId <= 0) {
            return EmployeeActionResult.Error(
                message = "Invalid employee ID."
            )
        }

        return try {

            val response =
                adminApi.deactivateEmployee(
                    authorization = "Bearer $token",
                    employeeId = employeeId
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                EmployeeActionResult.Success(
                    message = body.message,
                    employee = body.employee
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "This employee cannot be deactivated. Only active employees can be deactivated."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to deactivate employees."

                        404 ->
                            "The employee could not be found."

                        500 ->
                            "The server encountered an error while deactivating the employee."

                        else ->
                            "Unable to deactivate the employee."
                    }

                EmployeeActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            EmployeeActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // REACTIVATE EMPLOYEE
    // ========================================================

    suspend fun reactivateEmployee(
        token: String,
        employeeId: Int
    ): EmployeeActionResult {

        if (token.isBlank()) {
            return EmployeeActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (employeeId <= 0) {
            return EmployeeActionResult.Error(
                message = "Invalid employee ID."
            )
        }

        return try {

            val response =
                adminApi.reactivateEmployee(
                    authorization = "Bearer $token",
                    employeeId = employeeId
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                EmployeeActionResult.Success(
                    message = body.message,
                    employee = body.employee
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "This employee cannot be reactivated. Only inactive employees can be reactivated."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to reactivate employees."

                        404 ->
                            "The employee could not be found."

                        500 ->
                            "The server encountered an error while reactivating the employee."

                        else ->
                            "Unable to reactivate the employee."
                    }

                EmployeeActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            EmployeeActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // UPDATE EMPLOYEE ROLE
    // ========================================================

    suspend fun updateEmployeeRole(
        token: String,
        employeeId: Int,
        newRole: String
    ): EmployeeActionResult {

        if (token.isBlank()) {
            return EmployeeActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (employeeId <= 0) {
            return EmployeeActionResult.Error(
                message = "Invalid employee ID."
            )
        }

        if (newRole.isBlank()) {
            return EmployeeActionResult.Error(
                message = "Please select an employee role."
            )
        }

        val validRoles =
            listOf(
                "Admin",
                "Supervisor",
                "QualityController",
                "InventoryClerk",
                "ProductionManager"
            )

        val selectedRole =
            validRoles.firstOrNull { role ->

                role.equals(
                    newRole.trim(),
                    ignoreCase = true
                )
            }

        if (selectedRole == null) {
            return EmployeeActionResult.Error(
                message = "The selected employee role is invalid."
            )
        }

        return try {

            val response =
                adminApi.updateEmployeeRole(
                    authorization = "Bearer $token",
                    employeeId = employeeId,
                    request =
                        UpdateEmployeeRoleRequest(
                            role = selectedRole
                        )
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                EmployeeActionResult.Success(
                    message = body.message,
                    employee = body.employee
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "The employee role could not be changed. Check the selected role and try again."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to change employee roles."

                        404 ->
                            "The employee could not be found."

                        500 ->
                            "The server encountered an error while changing the employee role."

                        else ->
                            "Unable to change the employee role."
                    }

                EmployeeActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            EmployeeActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET ALL SHIFT TEAMS
    // ========================================================

    suspend fun getShiftTeams(
        token: String
    ): ShiftTeamsResult {

        if (token.isBlank()) {
            return ShiftTeamsResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getShiftTeams(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                ShiftTeamsResult.Success(
                    shiftTeams = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to view Shift Teams."

                        404 ->
                            "Shift Team management service was not found."

                        500 ->
                            "The server encountered an error while loading Shift Teams."

                        else ->
                            "Unable to load Shift Teams."
                    }

                ShiftTeamsResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamsResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET SHIFT TEAM BY ID
    // ========================================================

    suspend fun getShiftTeam(
        token: String,
        shiftTeamId: Int
    ): ShiftTeamResult {

        if (token.isBlank()) {
            return ShiftTeamResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (shiftTeamId <= 0) {
            return ShiftTeamResult.Error(
                message = "Invalid Shift Team ID."
            )
        }

        return try {

            val response =
                adminApi.getShiftTeam(
                    authorization = "Bearer $token",
                    shiftTeamId = shiftTeamId
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                ShiftTeamResult.Success(
                    shiftTeam = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to view this Shift Team."

                        404 ->
                            "The Shift Team could not be found."

                        500 ->
                            "The server encountered an error while loading the Shift Team."

                        else ->
                            "Unable to load the Shift Team."
                    }

                ShiftTeamResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // GET ACTIVE SUPERVISORS
    // ========================================================

    suspend fun getShiftTeamSupervisors(
        token: String
    ): ShiftTeamSupervisorsResult {

        if (token.isBlank()) {
            return ShiftTeamSupervisorsResult.Error(
                message = "Authentication token is missing."
            )
        }

        return try {

            val response =
                adminApi.getShiftTeamSupervisors(
                    authorization = "Bearer $token"
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                ShiftTeamSupervisorsResult.Success(
                    supervisors = response.body()!!
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to view Supervisors."

                        404 ->
                            "Supervisor service was not found."

                        500 ->
                            "The server encountered an error while loading Supervisors."

                        else ->
                            "Unable to load Supervisors."
                    }

                ShiftTeamSupervisorsResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamSupervisorsResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // CREATE SHIFT TEAM
    // ========================================================

    suspend fun createShiftTeam(
        token: String,
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        lineNumber: String?,
        totalEmployees: Int?,
        notes: String?
    ): ShiftTeamActionResult {

        if (token.isBlank()) {
            return ShiftTeamActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        val validationMessage =
            validateShiftTeamInput(
                shiftDate = shiftDate,
                shift = shift,
                supervisorId = supervisorId,
                totalEmployees = totalEmployees
            )

        if (validationMessage != null) {
            return ShiftTeamActionResult.Error(
                message = validationMessage
            )
        }

        val selectedShift =
            normalizeShift(shift)

        return try {

            val response =
                adminApi.createShiftTeam(
                    authorization = "Bearer $token",

                    request =
                        CreateShiftTeamRequest(
                            shiftDate = shiftDate.trim(),
                            shift = selectedShift,
                            supervisorID = supervisorId,
                            lineNumber =
                                lineNumber
                                    ?.trim()
                                    ?.takeIf { it.isNotEmpty() },
                            totalEmployees = totalEmployees,
                            notes =
                                notes
                                    ?.trim()
                                    ?.takeIf { it.isNotEmpty() }
                        )
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                ShiftTeamActionResult.Success(
                    message = body.message,
                    shiftTeam = body.shiftTeam
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "The Shift Team could not be created. Check the entered information and selected Supervisor."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to create Shift Teams."

                        500 ->
                            "The server encountered an error while creating the Shift Team."

                        else ->
                            "Unable to create the Shift Team."
                    }

                ShiftTeamActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // UPDATE SHIFT TEAM
    // ========================================================

    suspend fun updateShiftTeam(
        token: String,
        shiftTeamId: Int,
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        lineNumber: String?,
        totalEmployees: Int?,
        notes: String?
    ): ShiftTeamActionResult {

        if (token.isBlank()) {
            return ShiftTeamActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (shiftTeamId <= 0) {
            return ShiftTeamActionResult.Error(
                message = "Invalid Shift Team ID."
            )
        }

        val validationMessage =
            validateShiftTeamInput(
                shiftDate = shiftDate,
                shift = shift,
                supervisorId = supervisorId,
                totalEmployees = totalEmployees
            )

        if (validationMessage != null) {
            return ShiftTeamActionResult.Error(
                message = validationMessage
            )
        }

        val selectedShift =
            normalizeShift(shift)

        return try {

            val response =
                adminApi.updateShiftTeam(
                    authorization = "Bearer $token",

                    shiftTeamId = shiftTeamId,

                    request =
                        UpdateShiftTeamRequest(
                            shiftDate = shiftDate.trim(),
                            shift = selectedShift,
                            supervisorID = supervisorId,
                            lineNumber =
                                lineNumber
                                    ?.trim()
                                    ?.takeIf { it.isNotEmpty() },
                            totalEmployees = totalEmployees,
                            notes =
                                notes
                                    ?.trim()
                                    ?.takeIf { it.isNotEmpty() }
                        )
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                ShiftTeamActionResult.Success(
                    message = body.message,
                    shiftTeam = body.shiftTeam
                )

            } else {

                val message =
                    when (response.code()) {

                        400 ->
                            "The Shift Team could not be updated. Check the entered information and selected Supervisor."

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to update Shift Teams."

                        404 ->
                            "The Shift Team could not be found."

                        500 ->
                            "The server encountered an error while updating the Shift Team."

                        else ->
                            "Unable to update the Shift Team."
                    }

                ShiftTeamActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // DELETE SHIFT TEAM
    // ========================================================

    suspend fun deleteShiftTeam(
        token: String,
        shiftTeamId: Int
    ): ShiftTeamActionResult {

        if (token.isBlank()) {
            return ShiftTeamActionResult.Error(
                message = "Authentication token is missing."
            )
        }

        if (shiftTeamId <= 0) {
            return ShiftTeamActionResult.Error(
                message = "Invalid Shift Team ID."
            )
        }

        return try {

            val response =
                adminApi.deleteShiftTeam(
                    authorization = "Bearer $token",
                    shiftTeamId = shiftTeamId
                )

            if (
                response.isSuccessful &&
                response.body() != null
            ) {

                val body =
                    response.body()!!

                ShiftTeamActionResult.Success(
                    message = body.message,
                    shiftTeam = body.shiftTeam
                )

            } else {

                val message =
                    when (response.code()) {

                        401 ->
                            "Your session has expired. Please sign in again."

                        403 ->
                            "You do not have permission to delete Shift Teams."

                        404 ->
                            "The Shift Team could not be found."

                        500 ->
                            "The server encountered an error while deleting the Shift Team."

                        else ->
                            "Unable to delete the Shift Team."
                    }

                ShiftTeamActionResult.Error(
                    message = message
                )
            }

        } catch (exception: Exception) {

            ShiftTeamActionResult.Error(
                message =
                    "Unable to connect to the server. Please check your connection and try again."
            )
        }
    }


    // ========================================================
    // SHIFT TEAM INPUT VALIDATION
    // ========================================================

    private fun validateShiftTeamInput(
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        totalEmployees: Int?
    ): String? {

        if (shiftDate.isBlank()) {
            return "Please select a Shift Team date."
        }

        if (shift.isBlank()) {
            return "Please select a shift."
        }

        val validShift =
            listOf(
                "Morning",
                "Afternoon",
                "Night"
            ).any { value ->

                value.equals(
                    shift.trim(),
                    ignoreCase = true
                )
            }

        if (!validShift) {
            return "Please select Morning, Afternoon, or Night."
        }

        if (supervisorId <= 0) {
            return "Please select a Supervisor."
        }

        if (
            totalEmployees != null &&
            totalEmployees < 0
        ) {
            return "Total employees cannot be negative."
        }

        return null
    }


    // ========================================================
    // NORMALIZE SHIFT
    // ========================================================

    private fun normalizeShift(
        shift: String
    ): String {

        val validShifts =
            listOf(
                "Morning",
                "Afternoon",
                "Night"
            )

        return validShifts.first { value ->

            value.equals(
                shift.trim(),
                ignoreCase = true
            )
        }
    }
}