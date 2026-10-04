package com.sastellarsolutions.qaclothingfactory.ui.admin

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ShiftTeamSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AdminRepository
import kotlinx.coroutines.launch


// ============================================================
// SHIFT TEAMS SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftTeamsScreen(
    token: String,
    onBackClick: () -> Unit = {}
) {

    val repository =
        remember {
            AdminRepository()
        }

    val coroutineScope =
        rememberCoroutineScope()

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }


    // ========================================================
    // SCREEN STATE
    // ========================================================

    var shiftTeams by remember {
        mutableStateOf<List<ShiftTeamResponse>>(
            emptyList()
        )
    }

    var supervisors by remember {
        mutableStateOf<List<ShiftTeamSupervisorResponse>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }


    // ========================================================
    // DIALOG STATE
    // ========================================================

    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var editingShiftTeam by remember {
        mutableStateOf<ShiftTeamResponse?>(null)
    }

    var deletingShiftTeam by remember {
        mutableStateOf<ShiftTeamResponse?>(null)
    }


    // ========================================================
    // LOAD SHIFT TEAMS
    // ========================================================

    fun loadShiftTeams() {

        coroutineScope.launch {

            isLoading = true
            errorMessage = null

            when (
                val result =
                    repository.getShiftTeams(token)
            ) {

                is AdminRepository.ShiftTeamsResult.Success -> {

                    shiftTeams =
                        result.shiftTeams

                    errorMessage = null
                }

                is AdminRepository.ShiftTeamsResult.Error -> {

                    errorMessage =
                        result.message
                }
            }

            isLoading = false
        }
    }


    // ========================================================
    // LOAD SUPERVISORS
    // ========================================================

    fun loadSupervisors() {

        coroutineScope.launch {

            when (
                val result =
                    repository.getShiftTeamSupervisors(
                        token
                    )
            ) {

                is AdminRepository.ShiftTeamSupervisorsResult.Success -> {

                    supervisors =
                        result.supervisors
                }

                is AdminRepository.ShiftTeamSupervisorsResult.Error -> {

                    snackbarHostState.showSnackbar(
                        result.message
                    )
                }
            }
        }
    }


    // ========================================================
    // INITIAL LOAD
    // ========================================================

    LaunchedEffect(token) {

        loadShiftTeams()
        loadSupervisors()
    }


    // ========================================================
    // CREATE SHIFT TEAM
    // ========================================================

    fun createShiftTeam(
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        lineNumber: String?,
        totalEmployees: Int?,
        notes: String?
    ) {

        coroutineScope.launch {

            isProcessing = true

            when (
                val result =
                    repository.createShiftTeam(
                        token = token,
                        shiftDate = shiftDate,
                        shift = shift,
                        supervisorId = supervisorId,
                        lineNumber = lineNumber,
                        totalEmployees = totalEmployees,
                        notes = notes
                    )
            ) {

                is AdminRepository.ShiftTeamActionResult.Success -> {

                    showCreateDialog = false

                    snackbarHostState.showSnackbar(
                        result.message
                    )

                    loadShiftTeams()
                }

                is AdminRepository.ShiftTeamActionResult.Error -> {

                    snackbarHostState.showSnackbar(
                        result.message
                    )
                }
            }

            isProcessing = false
        }
    }


    // ========================================================
    // UPDATE SHIFT TEAM
    // ========================================================

    fun updateShiftTeam(
        shiftTeamId: Int,
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        lineNumber: String?,
        totalEmployees: Int?,
        notes: String?
    ) {

        coroutineScope.launch {

            isProcessing = true

            when (
                val result =
                    repository.updateShiftTeam(
                        token = token,
                        shiftTeamId = shiftTeamId,
                        shiftDate = shiftDate,
                        shift = shift,
                        supervisorId = supervisorId,
                        lineNumber = lineNumber,
                        totalEmployees = totalEmployees,
                        notes = notes
                    )
            ) {

                is AdminRepository.ShiftTeamActionResult.Success -> {

                    editingShiftTeam = null

                    snackbarHostState.showSnackbar(
                        result.message
                    )

                    loadShiftTeams()
                }

                is AdminRepository.ShiftTeamActionResult.Error -> {

                    snackbarHostState.showSnackbar(
                        result.message
                    )
                }
            }

            isProcessing = false
        }
    }


    // ========================================================
    // DELETE SHIFT TEAM
    // ========================================================

    fun deleteShiftTeam(
        shiftTeamId: Int
    ) {

        coroutineScope.launch {

            isProcessing = true

            when (
                val result =
                    repository.deleteShiftTeam(
                        token = token,
                        shiftTeamId = shiftTeamId
                    )
            ) {

                is AdminRepository.ShiftTeamActionResult.Success -> {

                    deletingShiftTeam = null

                    snackbarHostState.showSnackbar(
                        result.message
                    )

                    loadShiftTeams()
                }

                is AdminRepository.ShiftTeamActionResult.Error -> {

                    snackbarHostState.showSnackbar(
                        result.message
                    )
                }
            }

            isProcessing = false
        }
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Scaffold(

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Shift Teams",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            loadShiftTeams()
                            loadSupervisors()
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,
                            contentDescription =
                                "Refresh"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
            )
        }

    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        horizontal = 16.dp
                    )
        ) {

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            // =================================================
            // HEADER
            // =================================================

            Text(
                text = "Shift Team Management",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text =
                    "Manage production shifts, supervisors and production lines.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // =================================================
            // CREATE BUTTON
            // =================================================

            Button(
                onClick = {

                    loadSupervisors()

                    showCreateDialog = true
                },
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text = "Create Shift Team"
                )
            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // =================================================
            // CONTENT
            // =================================================

            when {

                isLoading -> {

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                errorMessage != null -> {

                    ShiftTeamsErrorState(
                        message =
                            errorMessage
                                ?: "Unable to load Shift Teams.",
                        onRetry = {
                            loadShiftTeams()
                        }
                    )
                }


                shiftTeams.isEmpty() -> {

                    EmptyShiftTeamsState(
                        onCreateClick = {

                            loadSupervisors()

                            showCreateDialog = true
                        }
                    )
                }


                else -> {

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {

                        items(
                            items = shiftTeams,
                            key = {
                                it.shiftTeamID
                            }
                        ) { shiftTeam ->

                            ShiftTeamCard(
                                shiftTeam =
                                    shiftTeam,

                                onEdit = {

                                    loadSupervisors()

                                    editingShiftTeam =
                                        shiftTeam
                                },

                                onDelete = {

                                    deletingShiftTeam =
                                        shiftTeam
                                }
                            )
                        }

                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }


    // ========================================================
    // CREATE DIALOG
    // ========================================================

    if (showCreateDialog) {

        ShiftTeamFormDialog(
            title =
                "Create Shift Team",

            supervisors =
                supervisors,

            existingShiftTeam =
                null,

            isProcessing =
                isProcessing,

            onDismiss = {

                if (!isProcessing) {
                    showCreateDialog = false
                }
            },

            onSave = {
                    shiftDate,
                    shift,
                    supervisorId,
                    lineNumber,
                    totalEmployees,
                    notes ->

                createShiftTeam(
                    shiftDate = shiftDate,
                    shift = shift,
                    supervisorId = supervisorId,
                    lineNumber = lineNumber,
                    totalEmployees = totalEmployees,
                    notes = notes
                )
            }
        )
    }


    // ========================================================
    // EDIT DIALOG
    // ========================================================

    editingShiftTeam?.let { shiftTeam ->

        ShiftTeamFormDialog(
            title =
                "Edit Shift Team",

            supervisors =
                supervisors,

            existingShiftTeam =
                shiftTeam,

            isProcessing =
                isProcessing,

            onDismiss = {

                if (!isProcessing) {
                    editingShiftTeam = null
                }
            },

            onSave = {
                    shiftDate,
                    shift,
                    supervisorId,
                    lineNumber,
                    totalEmployees,
                    notes ->

                updateShiftTeam(
                    shiftTeamId =
                        shiftTeam.shiftTeamID,
                    shiftDate =
                        shiftDate,
                    shift =
                        shift,
                    supervisorId =
                        supervisorId,
                    lineNumber =
                        lineNumber,
                    totalEmployees =
                        totalEmployees,
                    notes =
                        notes
                )
            }
        )
    }


    // ========================================================
    // DELETE CONFIRMATION
    // ========================================================

    deletingShiftTeam?.let { shiftTeam ->

        AlertDialog(

            onDismissRequest = {

                if (!isProcessing) {
                    deletingShiftTeam = null
                }
            },

            title = {

                Text(
                    text = "Delete Shift Team"
                )
            },

            text = {

                Text(
                    text =
                        "Are you sure you want to delete Shift Team #${shiftTeam.shiftTeamID}?"
                )
            },

            confirmButton = {

                TextButton(
                    enabled =
                        !isProcessing,

                    onClick = {

                        deleteShiftTeam(
                            shiftTeam.shiftTeamID
                        )
                    }
                ) {

                    if (isProcessing) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier
                                    .width(18.dp)
                                    .height(18.dp),
                            strokeWidth =
                                2.dp
                        )

                    } else {

                        Text(
                            text = "Delete"
                        )
                    }
                }
            },

            dismissButton = {

                TextButton(
                    enabled =
                        !isProcessing,

                    onClick = {
                        deletingShiftTeam = null
                    }
                ) {

                    Text(
                        text = "Cancel"
                    )
                }
            }
        )
    }
}


// ============================================================
// SHIFT TEAM CARD
// ============================================================

@Composable
private fun ShiftTeamCard(
    shiftTeam: ShiftTeamResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    18.dp
                )
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            shiftTeam.shift,
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            formatShiftDate(
                                shiftTeam.shiftDate
                            ),
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ShiftBadge(
                    shift =
                        shiftTeam.shift
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            ShiftTeamInformationRow(
                label = "Supervisor",
                value =
                    shiftTeam.supervisorName
            )

            ShiftTeamInformationRow(
                label = "Production Line",
                value =
                    shiftTeam.lineNumber
                        ?: "Not assigned"
            )

            ShiftTeamInformationRow(
                label = "Employees",
                value =
                    shiftTeam.totalEmployees
                        ?.toString()
                        ?: "Not specified"
            )


            if (
                !shiftTeam.notes.isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text = "Notes",
                    style =
                        MaterialTheme.typography.labelMedium,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        shiftTeam.notes,
                    style =
                        MaterialTheme.typography.bodyMedium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.End
            ) {

                OutlinedButton(
                    onClick =
                        onEdit
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Edit,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text = "Edit"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                OutlinedButton(
                    onClick =
                        onDelete,

                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                MaterialTheme.colorScheme.error
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(6.dp)
                    )

                    Text(
                        text = "Delete"
                    )
                }
            }
        }
    }
}


// ============================================================
// INFORMATION ROW
// ============================================================

@Composable
private fun ShiftTeamInformationRow(
    label: String,
    value: String
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                )
    ) {

        Text(
            text = "$label:",
            modifier =
                Modifier.width(
                    125.dp
                ),
            style =
                MaterialTheme.typography.bodyMedium,
            fontWeight =
                FontWeight.SemiBold
        )

        Text(
            text = value,
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme.typography.bodyMedium
        )
    }
}


// ============================================================
// SHIFT BADGE
// ============================================================

@Composable
private fun ShiftBadge(
    shift: String
) {

    Box(
        modifier =
            Modifier
                .background(
                    color =
                        MaterialTheme.colorScheme.secondaryContainer,
                    shape =
                        RoundedCornerShape(
                            50.dp
                        )
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
    ) {

        Text(
            text = shift,
            style =
                MaterialTheme.typography.labelMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}


// ============================================================
// EMPTY STATE
// ============================================================

@Composable
private fun EmptyShiftTeamsState(
    onCreateClick: () -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(
                    24.dp
                )
        ) {

            Text(
                text = "No Shift Teams",
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    "There are currently no production Shift Teams recorded.",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Button(
                onClick =
                    onCreateClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text = "Create Shift Team"
                )
            }
        }
    }
}


// ============================================================
// ERROR STATE
// ============================================================

@Composable
private fun ShiftTeamsErrorState(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier =
            Modifier.fillMaxSize(),
        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(
                    24.dp
                )
        ) {

            Text(
                text =
                    "Unable to load Shift Teams",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text =
                    message,
                style =
                    MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Button(
                onClick =
                    onRetry
            ) {

                Text(
                    text = "Try Again"
                )
            }
        }
    }
}


// ============================================================
// CREATE / EDIT FORM
// ============================================================

@Composable
private fun ShiftTeamFormDialog(
    title: String,
    supervisors: List<ShiftTeamSupervisorResponse>,
    existingShiftTeam: ShiftTeamResponse?,
    isProcessing: Boolean,

    onDismiss: () -> Unit,

    onSave: (
        shiftDate: String,
        shift: String,
        supervisorId: Int,
        lineNumber: String?,
        totalEmployees: Int?,
        notes: String?
    ) -> Unit
) {

    var shiftDate by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.shiftDate
                ?.take(10)
                ?: ""
        )
    }


    var selectedShift by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.shift
                ?: "Morning"
        )
    }


    var selectedSupervisorId by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.supervisorID
                ?: 0
        )
    }


    var lineNumber by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.lineNumber
                ?: ""
        )
    }


    var totalEmployeesText by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.totalEmployees
                ?.toString()
                ?: ""
        )
    }


    var notes by remember(
        existingShiftTeam
    ) {

        mutableStateOf(
            existingShiftTeam
                ?.notes
                ?: ""
        )
    }


    var shiftDropdownExpanded by remember {
        mutableStateOf(false)
    }


    var supervisorDropdownExpanded by remember {
        mutableStateOf(false)
    }


    var validationMessage by remember {
        mutableStateOf<String?>(null)
    }


    val selectedSupervisor =
        supervisors.firstOrNull {
            it.employeeID ==
                    selectedSupervisorId
        }


    AlertDialog(

        onDismissRequest = {

            if (!isProcessing) {
                onDismiss()
            }
        },

        title = {

            Text(
                text = title,
                fontWeight =
                    FontWeight.Bold
            )
        },

        text = {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {


                // =============================================
                // DATE
                // =============================================

                item {

                    OutlinedTextField(
                        value =
                            shiftDate,

                        onValueChange = {
                            shiftDate = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                text =
                                    "Shift Date"
                            )
                        },

                        placeholder = {
                            Text(
                                text =
                                    "YYYY-MM-DD"
                            )
                        },

                        singleLine =
                            true
                    )
                }


                // =============================================
                // SHIFT
                // =============================================

                item {

                    Column {

                        Text(
                            text = "Shift",
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            OutlinedButton(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                onClick = {
                                    shiftDropdownExpanded =
                                        true
                                }
                            ) {

                                Text(
                                    text =
                                        selectedShift,
                                    modifier =
                                        Modifier.weight(1f)
                                )
                            }

                            DropdownMenu(
                                expanded =
                                    shiftDropdownExpanded,

                                onDismissRequest = {
                                    shiftDropdownExpanded =
                                        false
                                }
                            ) {

                                listOf(
                                    "Morning",
                                    "Afternoon",
                                    "Night"
                                ).forEach { shift ->

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text =
                                                    shift
                                            )
                                        },

                                        onClick = {

                                            selectedShift =
                                                shift

                                            shiftDropdownExpanded =
                                                false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                // =============================================
                // SUPERVISOR
                // =============================================

                item {

                    Column {

                        Text(
                            text = "Supervisor",
                            style =
                                MaterialTheme.typography.labelMedium,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(5.dp)
                        )

                        Box(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            OutlinedButton(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                enabled =
                                    supervisors.isNotEmpty(),

                                onClick = {
                                    supervisorDropdownExpanded =
                                        true
                                }
                            ) {

                                Text(
                                    text =
                                        selectedSupervisor
                                            ?.fullName
                                            ?: if (
                                                supervisors.isEmpty()
                                            ) {
                                                "No active Supervisors"
                                            } else {
                                                "Select Supervisor"
                                            },

                                    modifier =
                                        Modifier.weight(1f)
                                )
                            }

                            DropdownMenu(
                                expanded =
                                    supervisorDropdownExpanded,

                                onDismissRequest = {
                                    supervisorDropdownExpanded =
                                        false
                                }
                            ) {

                                supervisors.forEach {
                                        supervisor ->

                                    DropdownMenuItem(
                                        text = {

                                            Column {

                                                Text(
                                                    text =
                                                        supervisor.fullName
                                                )

                                                Text(
                                                    text =
                                                        supervisor.emailAddress,
                                                    style =
                                                        MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        },

                                        onClick = {

                                            selectedSupervisorId =
                                                supervisor.employeeID

                                            supervisorDropdownExpanded =
                                                false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


                // =============================================
                // LINE NUMBER
                // =============================================

                item {

                    OutlinedTextField(
                        value =
                            lineNumber,

                        onValueChange = {
                            lineNumber = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                text =
                                    "Production Line"
                            )
                        },

                        placeholder = {
                            Text(
                                text =
                                    "Example: Line 1"
                            )
                        },

                        singleLine =
                            true
                    )
                }


                // =============================================
                // TOTAL EMPLOYEES
                // =============================================

                item {

                    OutlinedTextField(
                        value =
                            totalEmployeesText,

                        onValueChange = { value ->

                            if (
                                value.all {
                                    it.isDigit()
                                }
                            ) {
                                totalEmployeesText =
                                    value
                            }
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                text =
                                    "Total Employees"
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        singleLine =
                            true
                    )
                }


                // =============================================
                // NOTES
                // =============================================

                item {

                    OutlinedTextField(
                        value =
                            notes,

                        onValueChange = {
                            notes = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text(
                                text =
                                    "Notes"
                            )
                        },

                        minLines =
                            3,

                        maxLines =
                            5
                    )
                }


                // =============================================
                // VALIDATION MESSAGE
                // =============================================

                validationMessage?.let {
                        message ->

                    item {

                        Text(
                            text =
                                message,

                            color =
                                MaterialTheme.colorScheme.error,

                            style =
                                MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },


        // ====================================================
        // SAVE
        // ====================================================

        confirmButton = {

            TextButton(
                enabled =
                    !isProcessing,

                onClick = {

                    validationMessage =
                        null


                    when {

                        shiftDate.isBlank() -> {

                            validationMessage =
                                "Please enter the Shift Team date."
                        }


                        !isValidShiftDate(
                            shiftDate
                        ) -> {

                            validationMessage =
                                "Please enter the date as YYYY-MM-DD."
                        }


                        selectedSupervisorId <= 0 -> {

                            validationMessage =
                                "Please select a Supervisor."
                        }


                        totalEmployeesText.isNotBlank() &&
                                (
                                        totalEmployeesText
                                            .toIntOrNull() == null
                                        ) -> {

                            validationMessage =
                                "Please enter a valid number of employees."
                        }


                        else -> {

                            onSave(
                                shiftDate.trim(),
                                selectedShift,
                                selectedSupervisorId,
                                lineNumber
                                    .trim()
                                    .takeIf {
                                        it.isNotEmpty()
                                    },
                                totalEmployeesText
                                    .toIntOrNull(),
                                notes
                                    .trim()
                                    .takeIf {
                                        it.isNotEmpty()
                                    }
                            )
                        }
                    }
                }
            ) {

                if (isProcessing) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier
                                .width(18.dp)
                                .height(18.dp),
                        strokeWidth =
                            2.dp
                    )

                } else {

                    Text(
                        text =
                            if (
                                existingShiftTeam == null
                            ) {
                                "Create"
                            } else {
                                "Save Changes"
                            }
                    )
                }
            }
        },


        // ====================================================
        // CANCEL
        // ====================================================

        dismissButton = {

            TextButton(
                enabled =
                    !isProcessing,

                onClick =
                    onDismiss
            ) {

                Text(
                    text = "Cancel"
                )
            }
        }
    )
}


// ============================================================
// DATE VALIDATION
// ============================================================

private fun isValidShiftDate(
    date: String
): Boolean {

    val pattern =
        Regex(
            """^\d{4}-\d{2}-\d{2}$"""
        )

    return pattern.matches(
        date.trim()
    )
}


// ============================================================
// DISPLAY DATE
// ============================================================

private fun formatShiftDate(
    date: String
): String {

    return if (
        date.length >= 10
    ) {
        date.take(10)
    } else {
        date
    }
}