package com.sastellarsolutions.qaclothingfactory.ui.supervisor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdate
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorDailyUpdateRequest
import com.sastellarsolutions.qaclothingfactory.data.repository.SupervisorRepository
import java.text.DecimalFormat

@Composable
fun DailyUpdatesScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { SupervisorRepository() }

    var updates by remember {
        mutableStateOf<List<SupervisorDailyUpdate>>(emptyList())
    }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableStateOf(0) }
    var selectedUpdate by remember {
        mutableStateOf<SupervisorDailyUpdate?>(null)
    }
    var successMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadUpdates() {
        isLoading = true
        errorMessage = null

        repository.getDailyUpdates(token)
            .onSuccess { response ->
                updates = response.updates
            }
            .onFailure { error ->
                errorMessage = error.message ?: "Unable to load daily updates."
            }

        isLoading = false
    }

    LaunchedEffect(token, retryKey) {
        loadUpdates()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextButton(onClick = onBackClick) {
                    Text("Back")
                }

                Text(
                    text = "Daily Updates",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Record production progress for your assigned batches.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                DailyUpdatesLoading(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            errorMessage != null -> {
                DailyUpdatesError(
                    message = errorMessage ?: "Unable to load daily updates.",
                    onRetry = { retryKey++ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            updates.isEmpty() -> {
                DailyUpdatesEmpty(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    successMessage
                        ?.takeIf { it.isNotBlank() }
                        ?.let { message ->
                            item(key = "daily_update_success") {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = message,
                                        modifier = Modifier.padding(16.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                    item(key = "daily_updates_heading") {
                        Text(
                            text = "${updates.size} assigned batch${if (updates.size == 1) "" else "es"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = updates,
                        key = { update -> update.batchID }
                    ) { update ->
                        DailyUpdateCard(
                            update = update,
                            onUpdateClick = {
                                successMessage = null
                                selectedUpdate = update
                            }
                        )
                    }

                    item(key = "daily_updates_bottom_space") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    selectedUpdate?.let { update ->
        UpdateProgressDialog(
            update = update,
            token = token,
            repository = repository,
            onDismiss = {
                selectedUpdate = null
            },
            onUpdated = { message ->
                selectedUpdate = null
                successMessage = message
                retryKey++
            }
        )
    }
}

@Composable
private fun DailyUpdateCard(
    update: SupervisorDailyUpdate,
    onUpdateClick: () -> Unit
) {
    val progress = (update.progressPercentage / 100.0)
        .coerceIn(0.0, 1.0)
        .toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = update.batchNumber,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = update.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium
                    )

                    update.productCode
                        ?.takeIf { it.isNotBlank() }
                        ?.let { code ->
                            Text(
                                text = code,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                }

                Text(
                    text = update.productionStatus,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${formatDailyNumber(update.progressPercentage)}% complete",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider()

            DailyInfoRow(
                label = "Target",
                value = formatDailyNumber(update.targetQuantity)
            )
            DailyInfoRow(
                label = "Produced",
                value = formatDailyNumber(update.quantityProduced)
            )
            DailyInfoRow(
                label = "Defective",
                value = formatDailyNumber(update.quantityDefective)
            )
            DailyInfoRow(
                label = "Accepted",
                value = formatDailyNumber(update.acceptedQuantity)
            )

            HorizontalDivider()

            DailyInfoRow(
                label = "Production Date",
                value = formatDailyDate(update.productionDate)
            )
            DailyInfoRow(
                label = "Shift",
                value = update.shift ?: "Not specified"
            )
            DailyInfoRow(
                label = "Line",
                value = update.lineNumber ?: "Not specified"
            )
            DailyInfoRow(
                label = "Start Time",
                value = formatDailyTime(update.startTime)
            )
            DailyInfoRow(
                label = "End Time",
                value = formatDailyTime(update.endTime)
            )
            DailyInfoRow(
                label = "Quality",
                value = update.qualityStatus ?: "Pending"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onUpdateClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Progress")
            }
        }
    }
}

@Composable
private fun UpdateProgressDialog(
    update: SupervisorDailyUpdate,
    token: String,
    repository: SupervisorRepository,
    onDismiss: () -> Unit,
    onUpdated: (String) -> Unit
) {
    var quantityProduced by remember(update.batchID) {
        mutableStateOf(formatEditableNumber(update.quantityProduced))
    }
    var quantityDefective by remember(update.batchID) {
        mutableStateOf(formatEditableNumber(update.quantityDefective))
    }
    var productionStatus by remember(update.batchID) {
        mutableStateOf(update.productionStatus)
    }
    var startTime by remember(update.batchID) {
        mutableStateOf(formatEditableTime(update.startTime))
    }
    var endTime by remember(update.batchID) {
        mutableStateOf(formatEditableTime(update.endTime))
    }

    var statusMenuExpanded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf<String?>(null) }
    var saveRequest by remember { mutableStateOf<SupervisorDailyUpdateRequest?>(null) }

    val allowedStatuses = remember {
        listOf(
            "Planned",
            "In Progress",
            "Completed",
            "Cancelled"
        )
    }

    saveRequest?.let { request ->
        LaunchedEffect(request) {
            isSaving = true
            validationMessage = null

            repository.updateDailyProgress(
                token = token,
                batchId = update.batchID,
                request = request
            ).onSuccess { response ->
                isSaving = false
                saveRequest = null
                onUpdated(
                    response.message.ifBlank {
                        "Daily production progress updated successfully."
                    }
                )
            }.onFailure { error ->
                isSaving = false
                saveRequest = null
                validationMessage =
                    error.message ?: "Unable to update production progress."
            }
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Column {
                Text(
                    text = "Update Production Progress",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = update.batchNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = update.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                item {
                    Text(
                        text = "Target quantity: ${formatDailyNumber(update.targetQuantity)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                item {
                    OutlinedTextField(
                        value = quantityProduced,
                        onValueChange = { value ->
                            quantityProduced = value.filterDecimalInput()
                            validationMessage = null
                        },
                        label = {
                            Text("Quantity Produced")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        enabled = !isSaving
                    )
                }

                item {
                    OutlinedTextField(
                        value = quantityDefective,
                        onValueChange = { value ->
                            quantityDefective = value.filterDecimalInput()
                            validationMessage = null
                        },
                        label = {
                            Text("Quantity Defective")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        enabled = !isSaving
                    )
                }

                item {
                    Column {
                        Text(
                            text = "Production Status",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = {
                                statusMenuExpanded = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        ) {
                            Text(productionStatus)
                        }

                        DropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = {
                                statusMenuExpanded = false
                            }
                        ) {
                            allowedStatuses.forEach { status ->
                                DropdownMenuItem(
                                    text = {
                                        Text(status)
                                    },
                                    onClick = {
                                        productionStatus = status
                                        statusMenuExpanded = false
                                        validationMessage = null
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { value ->
                            startTime = value.filterTimeInput()
                            validationMessage = null
                        },
                        label = {
                            Text("Start Time")
                        },
                        supportingText = {
                            Text("Use HH:mm, for example 08:00")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        enabled = !isSaving
                    )
                }

                item {
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { value ->
                            endTime = value.filterTimeInput()
                            validationMessage = null
                        },
                        label = {
                            Text("End Time")
                        },
                        supportingText = {
                            Text("Use HH:mm, for example 12:30")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        enabled = !isSaving
                    )
                }

                validationMessage
                    ?.takeIf { it.isNotBlank() }
                    ?.let { message ->
                        item {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                if (isSaving) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val produced = quantityProduced.toDoubleOrNull()
                    val defective = quantityDefective.toDoubleOrNull()

                    validationMessage = validateDailyUpdate(
                        targetQuantity = update.targetQuantity,
                        produced = produced,
                        defective = defective,
                        startTime = startTime,
                        endTime = endTime
                    )

                    if (validationMessage == null) {
                        saveRequest = SupervisorDailyUpdateRequest(
                            quantityProduced = produced!!,
                            quantityDefective = defective!!,
                            productionStatus = productionStatus,
                            startTime = normalizeApiTime(startTime),
                            endTime = normalizeApiTime(endTime)
                        )
                    }
                },
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSaving
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun validateDailyUpdate(
    targetQuantity: Double,
    produced: Double?,
    defective: Double?,
    startTime: String,
    endTime: String
): String? {
    if (produced == null) {
        return "Enter a valid quantity produced."
    }

    if (defective == null) {
        return "Enter a valid quantity defective."
    }

    if (produced < 0.0) {
        return "Quantity produced cannot be negative."
    }

    if (defective < 0.0) {
        return "Quantity defective cannot be negative."
    }

    if (defective > produced) {
        return "Quantity defective cannot be greater than quantity produced."
    }

    if (targetQuantity > 0.0 && produced > targetQuantity) {
        return "Quantity produced cannot exceed the target quantity."
    }

    if (startTime.isNotBlank() && !isValidTime(startTime)) {
        return "Start time must use HH:mm format."
    }

    if (endTime.isNotBlank() && !isValidTime(endTime)) {
        return "End time must use HH:mm format."
    }

    if (
        startTime.isNotBlank() &&
        endTime.isNotBlank() &&
        isValidTime(startTime) &&
        isValidTime(endTime)
    ) {
        val startMinutes = timeToMinutes(startTime)
        val endMinutes = timeToMinutes(endTime)

        if (endMinutes < startMinutes) {
            return "End time cannot be earlier than start time."
        }
    }

    return null
}

@Composable
private fun DailyInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DailyUpdatesLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading daily updates...")
    }
}

@Composable
private fun DailyUpdatesError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun DailyUpdatesEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No daily updates available",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Assigned production batches will appear here when available.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatDailyNumber(value: Double): String {
    return DecimalFormat("#,##0.##").format(value)
}

private fun formatEditableNumber(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

private fun formatDailyDate(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Not specified"
    }

    return value
        .substringBefore("T")
        .takeIf { it.isNotBlank() }
        ?: value
}

private fun formatDailyTime(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Not specified"
    }

    return if (value.length >= 5) {
        value.substring(0, 5)
    } else {
        value
    }
}

private fun formatEditableTime(value: String?): String {
    if (value.isNullOrBlank()) {
        return ""
    }

    return if (value.length >= 5) {
        value.substring(0, 5)
    } else {
        value
    }
}

private fun String.filterDecimalInput(): String {
    val filtered = filter { character ->
        character.isDigit() || character == '.'
    }

    val firstDot = filtered.indexOf('.')

    return if (firstDot == -1) {
        filtered
    } else {
        filtered.substring(0, firstDot + 1) +
                filtered.substring(firstDot + 1).replace(".", "")
    }
}

private fun String.filterTimeInput(): String {
    val digits = filter { it.isDigit() }.take(4)

    return when {
        digits.length <= 2 -> digits
        else -> "${digits.take(2)}:${digits.drop(2)}"
    }
}

private fun isValidTime(value: String): Boolean {
    val match = Regex("""^([01]\d|2[0-3]):([0-5]\d)$""")
    return match.matches(value)
}

private fun timeToMinutes(value: String): Int {
    val parts = value.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].toInt()
    return (hour * 60) + minute
}

private fun normalizeApiTime(value: String): String? {
    if (value.isBlank()) {
        return null
    }

    return if (value.length == 5) {
        "$value:00"
    } else {
        value
    }
}

