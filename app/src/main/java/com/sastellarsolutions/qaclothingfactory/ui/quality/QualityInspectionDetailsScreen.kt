package com.sastellarsolutions.qaclothingfactory.ui.quality

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionDetails
import com.sastellarsolutions.qaclothingfactory.data.repository.QualityRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityInspectionDetailsScreen(
    token: String,
    batchID: Int,
    onBackClick: () -> Unit,
    onInspectionUpdated: () -> Unit
) {
    val repository = remember { QualityRepository() }

    var inspection by remember {
        mutableStateOf<QualityInspectionDetails?>(null)
    }
    var qualityNotes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }
    var pendingAction by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(token, batchID, refreshKey) {
        isLoading = true
        errorMessage = null

        when (
            val result = repository.getQualityInspection(
                token = token,
                batchID = batchID
            )
        ) {
            is QualityRepository.Result.Success -> {
                inspection = result.data
                qualityNotes = result.data.qualityNotes.orEmpty()
                isLoading = false
            }

            is QualityRepository.Result.Error -> {
                errorMessage = result.message
                isLoading = false
            }
        }
    }

    fun saveInspection(status: String) {
        if (isSaving) return

        if (status == "Rejected" && qualityNotes.isBlank()) {
            actionError = "Please provide quality notes before rejecting this batch."
            return
        }

        pendingAction = status
    }

    LaunchedEffect(pendingAction) {
        val status = pendingAction ?: return@LaunchedEffect

        isSaving = true
        actionError = null
        successMessage = null

        when (
            val result = repository.updateQualityInspection(
                token = token,
                batchID = batchID,
                qualityStatus = status,
                qualityNotes = qualityNotes
            )
        ) {
            is QualityRepository.Result.Success -> {
                successMessage = result.data.message
                isSaving = false
                pendingAction = null
                refreshKey++
            }

            is QualityRepository.Result.Error -> {
                actionError = result.message
                isSaving = false
                pendingAction = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Inspection Details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { refreshKey++ },
                        enabled = !isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMessage != null -> {
                    QualityDetailsErrorState(
                        message = errorMessage
                            ?: "Unable to load the quality inspection.",
                        onRetry = { refreshKey++ },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                inspection != null -> {
                    val currentInspection = inspection!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InspectionHeaderCard(
                            inspection = currentInspection
                        )

                        InspectionQuantityCard(
                            inspection = currentInspection
                        )

                        ProductionInformationCard(
                            inspection = currentInspection
                        )

                        QualityInformationCard(
                            inspection = currentInspection
                        )

                        val canInspect =
                            currentInspection.qualityStatus.equals(
                                "Pending",
                                ignoreCase = true
                            )

                        if (canInspect) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Inspection Decision",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "Review the production batch and record any quality findings before approving or rejecting it.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    OutlinedTextField(
                                        value = qualityNotes,
                                        onValueChange = {
                                            qualityNotes = it
                                            actionError = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        label = {
                                            Text(text = "Quality Notes")
                                        },
                                        placeholder = {
                                            Text(
                                                text = "Enter inspection findings..."
                                            )
                                        },
                                        minLines = 4,
                                        maxLines = 8,
                                        enabled = !isSaving
                                    )

                                    Text(
                                        text = "Notes are required when rejecting a batch.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (!actionError.isNullOrBlank()) {
                                        Text(
                                            text = actionError!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }

                                    if (!successMessage.isNullOrBlank()) {
                                        Text(
                                            text = successMessage!!,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    if (isSaving) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    saveInspection("Rejected")
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = "Reject")
                                            }

                                            Button(
                                                onClick = {
                                                    saveInspection("Approved")
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = "Approve")
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Inspection Completed",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "This batch has already been ${
                                            currentInspection.qualityStatus.lowercase()
                                        }.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    if (!currentInspection.qualityCheckedByName.isNullOrBlank()) {
                                        DetailsRow(
                                            label = "Inspected by",
                                            value = currentInspection.qualityCheckedByName
                                        )
                                    }

                                    if (!currentInspection.qualityCheckDate.isNullOrBlank()) {
                                        DetailsRow(
                                            label = "Inspection date",
                                            value = formatDateTime(
                                                currentInspection.qualityCheckDate
                                            )
                                        )
                                    }

                                    if (!currentInspection.qualityNotes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Quality Notes",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = currentInspection.qualityNotes,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    if (!successMessage.isNullOrBlank() && inspection != null) {
        val completed =
            !inspection!!.qualityStatus.equals(
                "Pending",
                ignoreCase = true
            )

        if (completed) {
            AlertDialog(
                onDismissRequest = {
                    successMessage = null
                },
                title = {
                    Text(text = "Inspection Saved")
                },
                text = {
                    Text(text = successMessage!!)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            successMessage = null
                            onInspectionUpdated()
                        }
                    ) {
                        Text(text = "Done")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            successMessage = null
                        }
                    ) {
                        Text(text = "Stay")
                    }
                }
            )
        }
    }
}

@Composable
private fun InspectionHeaderCard(
    inspection: QualityInspectionDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inspection.batchNumber,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = inspection.qualityStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = qualityStatusColor(
                        inspection.qualityStatus
                    )
                )
            }

            Text(
                text = inspection.productName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = inspection.productCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DetailsRow(
                label = "Production status",
                value = inspection.productionStatus
            )
        }
    }
}

@Composable
private fun InspectionQuantityCard(
    inspection: QualityInspectionDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Production Quantities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuantityValue(
                    label = "Target",
                    value = inspection.targetQuantity
                )

                QuantityValue(
                    label = "Produced",
                    value = inspection.quantityProduced
                )

                QuantityValue(
                    label = "Accepted",
                    value = inspection.acceptedQuantity
                )

                QuantityValue(
                    label = "Defective",
                    value = inspection.quantityDefective
                )
            }
        }
    }
}

@Composable
private fun ProductionInformationCard(
    inspection: QualityInspectionDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Production Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            DetailsRow(
                label = "Production date",
                value = formatDate(inspection.productionDate)
            )

            DetailsRow(
                label = "Shift",
                value = inspection.shift
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not specified"
            )

            DetailsRow(
                label = "Line",
                value = inspection.lineNumber
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not specified"
            )

            DetailsRow(
                label = "Supervisor",
                value = inspection.supervisorName
                    ?: "Not assigned"
            )

            DetailsRow(
                label = "Recorded by",
                value = inspection.recordedByName
                    ?: "Unknown"
            )

            if (!inspection.startTime.isNullOrBlank()) {
                DetailsRow(
                    label = "Start time",
                    value = inspection.startTime
                )
            }

            if (!inspection.endTime.isNullOrBlank()) {
                DetailsRow(
                    label = "End time",
                    value = inspection.endTime
                )
            }

            if (!inspection.rawMaterialUsed.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Raw Materials Used",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                val rawMaterials = parseQualityRawMaterials(inspection.rawMaterialUsed)

                if (rawMaterials.isEmpty()) {
                    Text(
                        text = "Not recorded",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    rawMaterials.forEach { material ->
                        DetailsRow(
                            label = material.material,
                            value = buildString {
                                append(formatQuantity(material.quantity))
                                if (material.unit.isNotBlank()) {
                                    append(" ")
                                    append(material.unit)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityInformationCard(
    inspection: QualityInspectionDetails
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Quality Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            DetailsRow(
                label = "Quality status",
                value = inspection.qualityStatus
            )

            DetailsRow(
                label = "Accepted quantity",
                value = formatQuantity(
                    inspection.acceptedQuantity
                )
            )

            DetailsRow(
                label = "Defective quantity",
                value = formatQuantity(
                    inspection.quantityDefective
                )
            )

            val defectRate =
                if (inspection.quantityProduced > 0.0) {
                    (
                            inspection.quantityDefective /
                                    inspection.quantityProduced
                            ) * 100.0
                } else {
                    0.0
                }

            DetailsRow(
                label = "Defect rate",
                value = "${formatPercentage(defectRate)}%"
            )

            if (!inspection.qualityCheckedByName.isNullOrBlank()) {
                DetailsRow(
                    label = "Quality controller",
                    value = inspection.qualityCheckedByName
                )
            }

            if (!inspection.qualityCheckDate.isNullOrBlank()) {
                DetailsRow(
                    label = "Checked on",
                    value = formatDateTime(
                        inspection.qualityCheckDate
                    )
                )
            }
        }
    }
}

@Composable
private fun QuantityValue(
    label: String,
    value: Double
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatQuantity(value),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DetailsRow(
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun QualityDetailsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load inspection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry
        ) {
            Text(text = "Try Again")
        }
    }
}

@Composable
private fun qualityStatusColor(
    status: String
) = when (status.lowercase()) {
    "approved" -> MaterialTheme.colorScheme.primary
    "rejected" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.secondary
}

private fun formatQuantity(
    value: Double
): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format("%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatPercentage(
    value: Double
): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format("%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatDate(
    value: String
): String {
    return value
        .substringBefore("T")
        .takeIf { it.isNotBlank() }
        ?: value
}

private fun formatDateTime(
    value: String
): String {
    return value
        .replace("T", " ")
        .substringBefore(".")
        .removeSuffix("Z")
}


// ============================================================
// RAW MATERIAL DISPLAY MODEL
// ============================================================

private data class QualityRawMaterialUsage(
    val material: String = "",
    val quantity: Double = 0.0,
    val unit: String = ""
)


// ============================================================
// PARSE RAW MATERIALS
// ============================================================

private fun parseQualityRawMaterials(
    rawMaterialUsed: String?
): List<QualityRawMaterialUsage> {

    if (rawMaterialUsed.isNullOrBlank()) {
        return emptyList()
    }

    return try {
        val type =
            object : TypeToken<List<QualityRawMaterialUsage>>() {}.type

        Gson().fromJson<List<QualityRawMaterialUsage>>(
            rawMaterialUsed,
            type
        ) ?: emptyList()

    } catch (_: Exception) {
        listOf(
            QualityRawMaterialUsage(
                material = rawMaterialUsed,
                quantity = 0.0,
                unit = ""
            )
        )
    }
}

