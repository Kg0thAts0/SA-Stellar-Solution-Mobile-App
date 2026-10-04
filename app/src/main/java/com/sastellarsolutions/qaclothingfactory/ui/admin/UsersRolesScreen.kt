package com.sastellarsolutions.qaclothingfactory.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.local.SessionManager
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminEmployeeResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AdminRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch


// ============================================================
// USERS & ROLES SCREEN
// ============================================================

@Composable
fun UsersRolesScreen(
    onBack: () -> Unit
) {

    BackHandler {
        onBack()
    }

    val context =
        LocalContext.current

    val coroutineScope =
        rememberCoroutineScope()

    val sessionManager =
        remember(context) {
            SessionManager(
                context = context
            )
        }

    val repository =
        remember {
            AdminRepository()
        }


    // ========================================================
    // EMPLOYEE LIST
    // ========================================================

    var employees by remember {
        mutableStateOf<List<AdminEmployeeResponse>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }

    var successMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    // ========================================================
    // EMPLOYEE ACTION STATES
    // ========================================================

    var approvingEmployeeId by remember {
        mutableStateOf<Int?>(
            null
        )
    }

    var deactivatingEmployeeId by remember {
        mutableStateOf<Int?>(
            null
        )
    }

    var reactivatingEmployeeId by remember {
        mutableStateOf<Int?>(
            null
        )
    }

    var updatingRoleEmployeeId by remember {
        mutableStateOf<Int?>(
            null
        )
    }


    // ========================================================
    // CONFIRMATION DIALOG STATES
    // ========================================================

    var employeeToDeactivate by remember {
        mutableStateOf<AdminEmployeeResponse?>(
            null
        )
    }

    var employeeToReactivate by remember {
        mutableStateOf<AdminEmployeeResponse?>(
            null
        )
    }


    // ========================================================
    // CHANGE ROLE DIALOG
    // ========================================================

    var employeeToChangeRole by remember {
        mutableStateOf<AdminEmployeeResponse?>(
            null
        )
    }

    var selectedRole by remember {
        mutableStateOf("")
    }


    // ========================================================
    // REFRESH TRIGGER
    // ========================================================

    var refreshTrigger by remember {
        mutableIntStateOf(0)
    }


    // ========================================================
    // LOAD EMPLOYEES
    // ========================================================

    LaunchedEffect(refreshTrigger) {

        isLoading = true
        errorMessage = null

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {

            errorMessage =
                "Your login session could not be found."

            isLoading = false

            return@LaunchedEffect
        }

        when (
            val result =
                repository.getEmployees(
                    token = token
                )
        ) {

            is AdminRepository.EmployeesResult.Success -> {

                employees =
                    result.employees

                errorMessage =
                    null
            }

            is AdminRepository.EmployeesResult.Error -> {

                employees =
                    emptyList()

                errorMessage =
                    result.message
            }
        }

        isLoading =
            false
    }


    // ========================================================
    // DEACTIVATION CONFIRMATION
    // ========================================================

    employeeToDeactivate?.let { employee ->

        AlertDialog(
            onDismissRequest = {

                if (deactivatingEmployeeId == null) {
                    employeeToDeactivate = null
                }
            },

            title = {

                Text(
                    text = "Deactivate employee?",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to deactivate ${employee.fullName}? " +
                                "They will no longer have an active employee account.",
                    fontFamily = MontserratFontFamily,
                    fontSize = 12.sp
                )
            },

            confirmButton = {

                TextButton(
                    enabled =
                        deactivatingEmployeeId == null,

                    onClick = {

                        successMessage = null
                        errorMessage = null

                        val token =
                            sessionManager.getToken()

                        if (token.isNullOrBlank()) {

                            errorMessage =
                                "Your login session could not be found."

                            employeeToDeactivate =
                                null

                            return@TextButton
                        }

                        deactivatingEmployeeId =
                            employee.employeeID

                        coroutineScope.launch {

                            when (
                                val result =
                                    repository.deactivateEmployee(
                                        token = token,
                                        employeeId = employee.employeeID
                                    )
                            ) {

                                is AdminRepository.EmployeeActionResult.Success -> {

                                    successMessage =
                                        result.message

                                    deactivatingEmployeeId =
                                        null

                                    employeeToDeactivate =
                                        null

                                    refreshTrigger++
                                }

                                is AdminRepository.EmployeeActionResult.Error -> {

                                    errorMessage =
                                        result.message

                                    deactivatingEmployeeId =
                                        null

                                    employeeToDeactivate =
                                        null
                                }
                            }
                        }
                    }
                ) {

                    if (
                        deactivatingEmployeeId ==
                        employee.employeeID
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier =
                                Modifier.size(8.dp)
                        )

                        Text(
                            text = "DEACTIVATING...",
                            fontFamily = MontserratFontFamily
                        )

                    } else {

                        Text(
                            text = "DEACTIVATE",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },

            dismissButton = {

                TextButton(
                    enabled =
                        deactivatingEmployeeId == null,

                    onClick = {
                        employeeToDeactivate = null
                    }
                ) {

                    Text(
                        text = "CANCEL",
                        fontFamily = MontserratFontFamily,
                        color = Color(0xFF555555)
                    )
                }
            }
        )
    }


    // ========================================================
    // REACTIVATION CONFIRMATION
    // ========================================================

    employeeToReactivate?.let { employee ->

        AlertDialog(
            onDismissRequest = {

                if (reactivatingEmployeeId == null) {
                    employeeToReactivate = null
                }
            },

            title = {

                Text(
                    text = "Reactivate employee?",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to reactivate ${employee.fullName}? " +
                                "Their employee account will become active again.",
                    fontFamily = MontserratFontFamily,
                    fontSize = 12.sp
                )
            },

            confirmButton = {

                TextButton(
                    enabled =
                        reactivatingEmployeeId == null,

                    onClick = {

                        successMessage = null
                        errorMessage = null

                        val token =
                            sessionManager.getToken()

                        if (token.isNullOrBlank()) {

                            errorMessage =
                                "Your login session could not be found."

                            employeeToReactivate =
                                null

                            return@TextButton
                        }

                        reactivatingEmployeeId =
                            employee.employeeID

                        coroutineScope.launch {

                            when (
                                val result =
                                    repository.reactivateEmployee(
                                        token = token,
                                        employeeId = employee.employeeID
                                    )
                            ) {

                                is AdminRepository.EmployeeActionResult.Success -> {

                                    successMessage =
                                        result.message

                                    reactivatingEmployeeId =
                                        null

                                    employeeToReactivate =
                                        null

                                    refreshTrigger++
                                }

                                is AdminRepository.EmployeeActionResult.Error -> {

                                    errorMessage =
                                        result.message

                                    reactivatingEmployeeId =
                                        null

                                    employeeToReactivate =
                                        null
                                }
                            }
                        }
                    }
                ) {

                    if (
                        reactivatingEmployeeId ==
                        employee.employeeID
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier =
                                Modifier.size(8.dp)
                        )

                        Text(
                            text = "REACTIVATING...",
                            fontFamily = MontserratFontFamily
                        )

                    } else {

                        Text(
                            text = "REACTIVATE",
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },

            dismissButton = {

                TextButton(
                    enabled =
                        reactivatingEmployeeId == null,

                    onClick = {
                        employeeToReactivate = null
                    }
                ) {

                    Text(
                        text = "CANCEL",
                        fontFamily = MontserratFontFamily,
                        color = Color(0xFF555555)
                    )
                }
            }
        )
    }


    // ========================================================
    // CHANGE / ASSIGN ROLE DIALOG
    // ========================================================

    employeeToChangeRole?.let { employee ->

        val roles =
            listOf(
                "Admin",
                "Supervisor",
                "QualityController",
                "InventoryClerk",
                "ProductionManager"
            )

        val employeeHasRole =
            !employee.role.isNullOrBlank()

        AlertDialog(
            onDismissRequest = {

                if (updatingRoleEmployeeId == null) {

                    employeeToChangeRole =
                        null

                    selectedRole =
                        ""
                }
            },

            title = {

                Text(
                    text =
                        if (employeeHasRole) {
                            "Change employee role"
                        } else {
                            "Assign employee role"
                        },
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            },

            text = {

                Column {

                    Text(
                        text = employee.fullName,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Current role: ${formatRole(employee.role)}",
                        fontFamily = MontserratFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF777777)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Text(
                        text =
                            if (employeeHasRole) {
                                "SELECT NEW ROLE"
                            } else {
                                "SELECT ROLE"
                            },
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF777777)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    roles.forEach { role ->

                        val isCurrentRole =
                            employee.role?.equals(
                                role,
                                ignoreCase = true
                            ) == true

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled =
                                        updatingRoleEmployeeId == null &&
                                                !isCurrentRole
                                ) {
                                    selectedRole = role
                                }
                                .padding(
                                    vertical = 5.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected =
                                    selectedRole == role,

                                onClick = {

                                    if (
                                        updatingRoleEmployeeId == null &&
                                        !isCurrentRole
                                    ) {
                                        selectedRole = role
                                    }
                                },

                                enabled =
                                    updatingRoleEmployeeId == null &&
                                            !isCurrentRole
                            )

                            Column {

                                Text(
                                    text =
                                        formatRole(role),
                                    fontFamily = MontserratFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    color =
                                        if (isCurrentRole) {
                                            Color(0xFF999999)
                                        } else {
                                            Color(0xFF222222)
                                        }
                                )

                                if (isCurrentRole) {

                                    Text(
                                        text = "CURRENT ROLE",
                                        fontFamily = MontserratFontFamily,
                                        fontSize = 8.sp,
                                        letterSpacing = 0.7.sp,
                                        color = Color(0xFF999999)
                                    )
                                }
                            }
                        }
                    }
                }
            },

            confirmButton = {

                TextButton(
                    enabled =
                        selectedRole.isNotBlank() &&
                                updatingRoleEmployeeId == null,

                    onClick = {

                        if (selectedRole.isBlank()) {
                            return@TextButton
                        }

                        successMessage = null
                        errorMessage = null

                        val token =
                            sessionManager.getToken()

                        if (token.isNullOrBlank()) {

                            errorMessage =
                                "Your login session could not be found."

                            employeeToChangeRole =
                                null

                            selectedRole =
                                ""

                            return@TextButton
                        }

                        updatingRoleEmployeeId =
                            employee.employeeID

                        coroutineScope.launch {

                            when (
                                val result =
                                    repository.updateEmployeeRole(
                                        token = token,
                                        employeeId = employee.employeeID,
                                        newRole = selectedRole
                                    )
                            ) {

                                is AdminRepository.EmployeeActionResult.Success -> {

                                    successMessage =
                                        result.message

                                    updatingRoleEmployeeId =
                                        null

                                    employeeToChangeRole =
                                        null

                                    selectedRole =
                                        ""

                                    refreshTrigger++
                                }

                                is AdminRepository.EmployeeActionResult.Error -> {

                                    errorMessage =
                                        result.message

                                    updatingRoleEmployeeId =
                                        null

                                    employeeToChangeRole =
                                        null

                                    selectedRole =
                                        ""
                                }
                            }
                        }
                    }
                ) {

                    if (
                        updatingRoleEmployeeId ==
                        employee.employeeID
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )

                        Spacer(
                            modifier =
                                Modifier.size(8.dp)
                        )

                        Text(
                            text =
                                if (employeeHasRole) {
                                    "UPDATING..."
                                } else {
                                    "ASSIGNING..."
                                },
                            fontFamily = MontserratFontFamily
                        )

                    } else {

                        Text(
                            text =
                                if (employeeHasRole) {
                                    "UPDATE ROLE"
                                } else {
                                    "ASSIGN ROLE"
                                },
                            fontFamily = MontserratFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },

            dismissButton = {

                TextButton(
                    enabled =
                        updatingRoleEmployeeId == null,

                    onClick = {

                        employeeToChangeRole =
                            null

                        selectedRole =
                            ""
                    }
                ) {

                    Text(
                        text = "CANCEL",
                        fontFamily = MontserratFontFamily,
                        color = Color(0xFF555555)
                    )
                }
            }
        )
    }


    // ========================================================
    // MAIN SCREEN
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF8F8F8)
            )
    ) {

        // ====================================================
        // HEADER
        // ====================================================

        Surface(
            modifier =
                Modifier.fillMaxWidth(),
            color =
                Color.White,
            shadowElevation =
                2.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 16.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .clickable {
                            onBack()
                        }
                        .padding(
                            horizontal = 4.dp,
                            vertical = 10.dp
                        ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "←",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF111111)
                    )

                    Spacer(
                        modifier =
                            Modifier.size(7.dp)
                    )

                    Text(
                        text = "BACK",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                Text(
                    text = "USERS & ROLES",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color(0xFF111111)
                )

                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )

                Text(
                    text =
                        "Manage employee accounts and system permissions.",
                    fontFamily = MontserratFontFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF777777)
                )
            }
        }


        // ====================================================
        // CONTENT
        // ====================================================

        when {

            // =================================================
            // INITIAL LOADING
            // =================================================

            isLoading &&
                    employees.isEmpty() -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(34.dp),
                        color =
                            Color(0xFF111111)
                    )
                }
            }


            // =================================================
            // ERROR
            // =================================================

            errorMessage != null &&
                    employees.isEmpty() -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    Color.White
                            ),
                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation =
                                    1.dp
                            )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {

                            Text(
                                text =
                                    "Unable to load employees",
                                fontFamily =
                                    MontserratFontFamily,
                                fontWeight =
                                    FontWeight.SemiBold,
                                fontSize =
                                    14.sp,
                                color =
                                    Color(0xFF111111)
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(7.dp)
                            )

                            Text(
                                text =
                                    errorMessage
                                        ?: "An unexpected error occurred.",
                                fontFamily =
                                    MontserratFontFamily,
                                fontSize =
                                    11.sp,
                                color =
                                    Color(0xFF777777)
                            )
                        }
                    }
                }
            }


            // =================================================
            // EMPTY
            // =================================================

            employees.isEmpty() -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            "No employees were found.",
                        fontFamily =
                            MontserratFontFamily,
                        fontSize =
                            12.sp,
                        color =
                            Color(0xFF777777)
                    )
                }
            }


            // =================================================
            // EMPLOYEE LIST
            // =================================================

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 24.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(18.dp)
                        )
                    }


                    if (successMessage != null) {

                        item {

                            MessageCard(
                                message =
                                    successMessage ?: "",
                                isError =
                                    false
                            )
                        }
                    }


                    if (errorMessage != null) {

                        item {

                            MessageCard(
                                message =
                                    errorMessage ?: "",
                                isError =
                                    true
                            )
                        }
                    }


                    item {

                        Text(
                            text =
                                "${employees.size} EMPLOYEES",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.2.sp,
                            color =
                                Color(0xFF777777)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )
                    }


                    items(
                        items =
                            employees,
                        key = { employee ->
                            employee.employeeID
                        }
                    ) { employee ->


                        val isApproving =
                            approvingEmployeeId ==
                                    employee.employeeID

                        val isDeactivating =
                            deactivatingEmployeeId ==
                                    employee.employeeID

                        val isReactivating =
                            reactivatingEmployeeId ==
                                    employee.employeeID

                        val isUpdatingRole =
                            updatingRoleEmployeeId ==
                                    employee.employeeID

                        val isAnyActionRunning =
                            approvingEmployeeId != null ||
                                    deactivatingEmployeeId != null ||
                                    reactivatingEmployeeId != null ||
                                    updatingRoleEmployeeId != null


                        EmployeeCard(
                            employee = employee,

                            isApproving =
                                isApproving,

                            isDeactivating =
                                isDeactivating,

                            isReactivating =
                                isReactivating,

                            isUpdatingRole =
                                isUpdatingRole,

                            actionsEnabled =
                                !isAnyActionRunning,

                            onApprove = {

                                if (isAnyActionRunning) {
                                    return@EmployeeCard
                                }


                                // A pending employee must have a role
                                // before the account can be approved.
                                if (employee.role.isNullOrBlank()) {

                                    errorMessage =
                                        "Assign a role to ${employee.fullName} before approving the account."

                                    successMessage =
                                        null

                                    return@EmployeeCard
                                }


                                successMessage =
                                    null

                                errorMessage =
                                    null


                                val token =
                                    sessionManager.getToken()


                                if (token.isNullOrBlank()) {

                                    errorMessage =
                                        "Your login session could not be found."

                                    return@EmployeeCard
                                }


                                approvingEmployeeId =
                                    employee.employeeID


                                coroutineScope.launch {

                                    when (
                                        val result =
                                            repository.approveEmployee(
                                                token = token,
                                                employeeId =
                                                    employee.employeeID
                                            )
                                    ) {

                                        is AdminRepository.EmployeeActionResult.Success -> {

                                            successMessage =
                                                result.message

                                            approvingEmployeeId =
                                                null

                                            refreshTrigger++
                                        }


                                        is AdminRepository.EmployeeActionResult.Error -> {

                                            errorMessage =
                                                result.message

                                            approvingEmployeeId =
                                                null
                                        }
                                    }
                                }
                            },

                            onDeactivate = {

                                if (isAnyActionRunning) {
                                    return@EmployeeCard
                                }

                                successMessage =
                                    null

                                errorMessage =
                                    null

                                employeeToDeactivate =
                                    employee
                            },

                            onReactivate = {

                                if (isAnyActionRunning) {
                                    return@EmployeeCard
                                }

                                successMessage =
                                    null

                                errorMessage =
                                    null

                                employeeToReactivate =
                                    employee
                            },

                            onChangeRole = {

                                if (isAnyActionRunning) {
                                    return@EmployeeCard
                                }

                                successMessage =
                                    null

                                errorMessage =
                                    null

                                selectedRole =
                                    ""

                                employeeToChangeRole =
                                    employee
                            }
                        )
                    }


                    item {

                        Spacer(
                            modifier =
                                Modifier.height(30.dp)
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// MESSAGE CARD
// ============================================================

@Composable
private fun MessageCard(
    message: String,
    isError: Boolean
) {

    val backgroundColor =
        if (isError) {
            Color(0xFFFFEBEE)
        } else {
            Color(0xFFE8F5E9)
        }

    val textColor =
        if (isError) {
            Color(0xFFC62828)
        } else {
            Color(0xFF2E7D32)
        }


    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    backgroundColor
            )
    ) {

        Text(
            text =
                message,
            modifier =
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
            fontFamily =
                MontserratFontFamily,
            fontWeight =
                FontWeight.Medium,
            fontSize =
                10.sp,
            color =
                textColor
        )
    }
}


// ============================================================
// EMPLOYEE CARD
// ============================================================

@Composable
private fun EmployeeCard(
    employee: AdminEmployeeResponse,
    isApproving: Boolean,
    isDeactivating: Boolean,
    isReactivating: Boolean,
    isUpdatingRole: Boolean,
    actionsEnabled: Boolean,
    onApprove: () -> Unit,
    onDeactivate: () -> Unit,
    onReactivate: () -> Unit,
    onChangeRole: () -> Unit
) {

    val isPending =
        employee.employeeStatus
            ?.equals(
                "Pending",
                ignoreCase = true
            ) == true

    val isActive =
        employee.employeeStatus
            ?.equals(
                "Active",
                ignoreCase = true
            ) == true

    val isInactive =
        employee.employeeStatus
            ?.equals(
                "Inactive",
                ignoreCase = true
            ) == true

    val hasRole =
        !employee.role.isNullOrBlank()


    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {


            // =================================================
            // NAME + STATUS
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        employee.fullName,
                    fontFamily =
                        MontserratFontFamily,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize =
                        14.sp,
                    color =
                        Color(0xFF111111),
                    modifier =
                        Modifier.weight(1f)
                )

                Spacer(
                    modifier =
                        Modifier.size(10.dp)
                )

                EmployeeStatusBadge(
                    status =
                        employee.employeeStatus
                            ?: "Unknown"
                )
            }


            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )


            // =================================================
            // EMAIL
            // =================================================

            Text(
                text =
                    employee.emailAddress,
                fontFamily =
                    MontserratFontFamily,
                fontSize =
                    11.sp,
                color =
                    Color(0xFF777777)
            )


            Spacer(
                modifier =
                    Modifier.height(15.dp)
            )


            // =================================================
            // ROLE
            // =================================================

            Text(
                text =
                    "ROLE",
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize =
                    9.sp,
                letterSpacing =
                    1.sp,
                color =
                    Color(0xFF999999)
            )


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )


            Text(
                text =
                    formatRole(
                        employee.role
                    ),
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.Medium,
                fontSize =
                    11.sp,
                color =
                    if (hasRole) {
                        Color(0xFF333333)
                    } else {
                        Color(0xFFF57F17)
                    }
            )


            // =================================================
            // ASSIGN / CHANGE ROLE
            // =================================================

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            Button(
                onClick = {
                    onChangeRole()
                },
                enabled =
                    actionsEnabled &&
                            !isUpdatingRole,
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF333333),

                        contentColor =
                            Color.White,

                        disabledContainerColor =
                            Color(0xFFAAAAAA),

                        disabledContentColor =
                            Color.White
                    )
            ) {

                if (isUpdatingRole) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(18.dp),
                        strokeWidth =
                            2.dp,
                        color =
                            Color.White
                    )

                    Spacer(
                        modifier =
                            Modifier.size(10.dp)
                    )

                    Text(
                        text =
                            if (hasRole) {
                                "UPDATING ROLE..."
                            } else {
                                "ASSIGNING ROLE..."
                            },
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            10.sp,
                        letterSpacing =
                            1.sp
                    )

                } else {

                    Text(
                        text =
                            if (hasRole) {
                                "CHANGE ROLE"
                            } else {
                                "ASSIGN ROLE"
                            },
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            10.sp,
                        letterSpacing =
                            1.sp
                    )
                }
            }


            // =================================================
            // CREATED BY
            // =================================================

            if (employee.createdBy != null) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Created by employee #${employee.createdBy}",
                    fontFamily =
                        MontserratFontFamily,
                    fontSize =
                        9.sp,
                    color =
                        Color(0xFF999999)
                )
            }


            // =================================================
            // PENDING EMPLOYEE
            // =================================================

            if (isPending) {

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                // ---------------------------------------------
                // Explain why approval is disabled
                // ---------------------------------------------

                if (!hasRole) {

                    Text(
                        text =
                            "Assign a role before approving this employee.",
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.Medium,
                        fontSize =
                            10.sp,
                        color =
                            Color(0xFFF57F17)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )
                }


                Button(
                    onClick = {
                        onApprove()
                    },
                    enabled =
                        actionsEnabled &&
                                !isApproving &&
                                hasRole,
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF111111),

                            contentColor =
                                Color.White,

                            disabledContainerColor =
                                Color(0xFFAAAAAA),

                            disabledContentColor =
                                Color.White
                        )
                ) {

                    if (isApproving) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(18.dp),
                            strokeWidth =
                                2.dp,
                            color =
                                Color.White
                        )

                        Spacer(
                            modifier =
                                Modifier.size(10.dp)
                        )

                        Text(
                            text =
                                "APPROVING...",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )

                    } else {

                        Text(
                            text =
                                if (hasRole) {
                                    "APPROVE EMPLOYEE"
                                } else {
                                    "ROLE REQUIRED"
                                },
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )
                    }
                }
            }


            // =================================================
            // ACTIVE -> DEACTIVATE
            // =================================================

            if (isActive) {

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Button(
                    onClick = {
                        onDeactivate()
                    },
                    enabled =
                        actionsEnabled &&
                                !isDeactivating,
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFFC62828),

                            contentColor =
                                Color.White,

                            disabledContainerColor =
                                Color(0xFFAAAAAA),

                            disabledContentColor =
                                Color.White
                        )
                ) {

                    if (isDeactivating) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(18.dp),
                            strokeWidth =
                                2.dp,
                            color =
                                Color.White
                        )

                        Spacer(
                            modifier =
                                Modifier.size(10.dp)
                        )

                        Text(
                            text =
                                "DEACTIVATING...",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )

                    } else {

                        Text(
                            text =
                                "DEACTIVATE EMPLOYEE",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )
                    }
                }
            }


            // =================================================
            // INACTIVE -> REACTIVATE
            // =================================================

            if (isInactive) {

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )

                Button(
                    onClick = {
                        onReactivate()
                    },
                    enabled =
                        actionsEnabled &&
                                !isReactivating,
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF2E7D32),

                            contentColor =
                                Color.White,

                            disabledContainerColor =
                                Color(0xFFAAAAAA),

                            disabledContentColor =
                                Color.White
                        )
                ) {

                    if (isReactivating) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(18.dp),
                            strokeWidth =
                                2.dp,
                            color =
                                Color.White
                        )

                        Spacer(
                            modifier =
                                Modifier.size(10.dp)
                        )

                        Text(
                            text =
                                "REACTIVATING...",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )

                    } else {

                        Text(
                            text =
                                "REACTIVATE EMPLOYEE",
                            fontFamily =
                                MontserratFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize =
                                10.sp,
                            letterSpacing =
                                1.sp
                        )
                    }
                }
            }
        }
    }
}


// ============================================================
// EMPLOYEE STATUS BADGE
// ============================================================

@Composable
private fun EmployeeStatusBadge(
    status: String
) {

    val backgroundColor =
        when (
            status.lowercase()
        ) {

            "active" -> {
                Color(0xFFE8F5E9)
            }

            "pending" -> {
                Color(0xFFFFF8E1)
            }

            "inactive" -> {
                Color(0xFFF3F3F3)
            }

            else -> {
                Color(0xFFF3F3F3)
            }
        }


    val textColor =
        when (
            status.lowercase()
        ) {

            "active" -> {
                Color(0xFF2E7D32)
            }

            "pending" -> {
                Color(0xFFF57F17)
            }

            "inactive" -> {
                Color(0xFF666666)
            }

            else -> {
                Color(0xFF666666)
            }
        }


    Box(
        modifier =
            Modifier.background(
                color =
                    backgroundColor,
                shape =
                    RoundedCornerShape(50.dp)
            )
    ) {

        Text(
            text =
                status.uppercase(),
            fontFamily =
                MontserratFontFamily,
            fontWeight =
                FontWeight.SemiBold,
            fontSize =
                8.sp,
            letterSpacing =
                0.6.sp,
            color =
                textColor,
            modifier =
                Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        )
    }
}


// ============================================================
// FORMAT EMPLOYEE ROLE
// ============================================================

private fun formatRole(
    role: String?
): String {

    if (role.isNullOrBlank()) {
        return "No role assigned"
    }

    return when (role) {

        "Admin" -> {
            "Administrator"
        }

        "ProductionManager" -> {
            "Production Manager"
        }

        "QualityController" -> {
            "Quality Controller"
        }

        "InventoryClerk" -> {
            "Inventory Clerk"
        }

        "Supervisor" -> {
            "Supervisor"
        }

        else -> {
            role
        }
    }
}